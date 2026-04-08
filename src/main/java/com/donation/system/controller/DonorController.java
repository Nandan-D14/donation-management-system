package com.donation.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.donation.system.model.entity.Donor;
import com.donation.system.service.DonorService;

@Controller
@RequestMapping("/donor")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard() {
        return "donor/dashboard";
    }

    // Show donate page
    @GetMapping("/donate")
    public String donatePage() {
        return "donor/donate";
    }

    // Save donor to DB
    @PostMapping("/donate")
    public String donate(@ModelAttribute Donor donor) {
        donorService.saveDonor(donor);
        return "redirect:/donor/status";
    }

    // Show donation status
    @GetMapping("/status")
    public String status(Model model) {
        model.addAttribute("donations", donorService.getAll());
        return "donor/status";
    }
}