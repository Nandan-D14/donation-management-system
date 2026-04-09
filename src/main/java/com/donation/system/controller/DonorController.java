package com.donation.system.controller;

import com.donation.system.model.entity.Donor;
import com.donation.system.repository.DonorRepository;
import com.donation.system.service.DonorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Donor flow controller.
 *
 * @author Team
 */
@Controller
@RequestMapping("/donor")
public class DonorController {

    private final DonorService donorService;
    private final DonorRepository donorRepository;

    public DonorController(DonorService donorService, DonorRepository donorRepository) {
        this.donorService = donorService;
        this.donorRepository = donorRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!"DONOR".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("userName", session.getAttribute("userName"));
        return "donor/dashboard";
    }

    @GetMapping("/donate")
    public String donatePage(HttpSession session) {
        if (!"DONOR".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        return "donor/donate";
    }

    @PostMapping("/donate")
    public String donate(HttpSession session,
                         @RequestParam String donationType,
                         @RequestParam(required = false) String bloodType,
                         @RequestParam(required = false) String organType,
                         @RequestParam int quantity,
                         Model model) {
        if (!"DONOR".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }

        String mail = (String) session.getAttribute("userMail");
        Optional<Donor> donorOptional = donorRepository.findByMail(mail);
        if (donorOptional.isEmpty()) {
            model.addAttribute("error", "Donor account not found.");
            return "donor/donate";
        }

        donorService.recordDonation(donorOptional.get(), donationType, bloodType, organType, quantity);
        return "redirect:/donor/status";
    }

    @GetMapping("/status")
    public String status(HttpSession session, Model model) {
        if (!"DONOR".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }

        String mail = (String) session.getAttribute("userMail");
        Optional<Donor> donorOptional = donorRepository.findByMail(mail);
        if (donorOptional.isEmpty()) {
            return "redirect:/login";
        }

        Donor donor = donorOptional.get();
        model.addAttribute("donations", donorService.getDonationsForDonor(donor));
        return "donor/status";
    }
}
