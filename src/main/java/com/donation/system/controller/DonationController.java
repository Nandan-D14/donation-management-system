package com.donation.system.controller;

import com.donation.system.model.entity.Donation;
import com.donation.system.service.DonationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * GRASP: Controller Pattern
 * DonationController handles all system events related to Donation recording and status updates.
 * Delegates to DonationService (Information Expert) and EventManager (Singleton).
 *
 * @author Nandan (SRN 363)
 */
@Controller
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping
    public String listDonations(Model model) {
        model.addAttribute("donations", donationService.getAllDonations());
        return "donations/list";
    }

    @GetMapping("/add")
    public String addDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        return "donations/add";
    }

    @PostMapping("/add")
    public String addDonation(@ModelAttribute Donation donation) {
        donation.setDate(new Date());
        if (donation.getStatus() == null) {
            donation.setStatus("PENDING");
        }
        donationService.recordDonation(donation);
        return "redirect:/donations";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable int id, @RequestParam String status) {
        donationService.updateStatus(id, status);
        return "redirect:/donations";
    }
}
