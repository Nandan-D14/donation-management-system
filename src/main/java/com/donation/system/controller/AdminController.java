package com.donation.system.controller;

import com.donation.system.model.entity.Admin;
import com.donation.system.model.entity.Donation;
import com.donation.system.service.AdminService;
import com.donation.system.service.DonationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * GRASP: Controller Pattern
 * AdminController is the primary controller for Nandan's module.
 * It receives all HTTP requests for admin and donation operations and delegates
 * the business logic to AdminService and DonationService.
 *
 * @author Nandan (SRN 363)
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final DonationService donationService;

    public AdminController(AdminService adminService, DonationService donationService) {
        this.adminService = adminService;
        this.donationService = donationService;
    }

    // === Dashboard (single entry point) ===

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("message", "Welcome to Admin Dashboard");
        model.addAttribute("donationCount", donationService.getAllDonations().size());
        model.addAttribute("adminCount", adminService.getAllAdmins().size());
        model.addAttribute("eventCount", donationService.getEventCount());
        return "admin/dashboard";
    }

    // === Manage Users ===

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin/list";
    }

    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin/add";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute Admin admin) {
        adminService.saveAdmin(admin);
        return "redirect:/admin/users";
    }

    // === Record Donations ===

    @GetMapping("/donations/add")
    public String recordDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        return "donations/add";
    }

    @PostMapping("/donations/add")
    public String recordDonation(@ModelAttribute Donation donation) {
        donationService.recordDonation(donation);
        return "redirect:/admin/donations";
    }

    @PostMapping("/donations/update-status/{id}")
    public String updateDonationStatus(@PathVariable int id, @RequestParam String status) {
        donationService.updateStatus(id, status);
        return "redirect:/admin/donations";
    }

    // === View Donations ===

    @GetMapping("/donations")
    public String listDonations(Model model) {
        model.addAttribute("donations", donationService.getAllDonations());
        return "donations/list";
    }

    // === Generate Reports ===

    @GetMapping("/reports")
    public String generateReports(Model model) {
        model.addAttribute("donations", donationService.getAllDonations());
        model.addAttribute("donationCount", donationService.getDonationCount());
        model.addAttribute("eventCount", donationService.getEventCount());
        return "admin/reports";
    }
}
