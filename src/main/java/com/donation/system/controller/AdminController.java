package com.donation.system.controller;

import com.donation.system.model.entity.Admin;
import com.donation.system.service.AdminService;
import com.donation.system.service.DonationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * GRASP: Controller Principle
 * Admin acts as the single system controller — receives ALL incoming requests
 * and delegates to appropriate handlers (Donation, User, Inventory modules).
 * Admin does NOT do the work itself, it coordinates.
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

    @GetMapping("/donations/record")
    public String recordDonationForm(Model model) {
        model.addAttribute("donation", new com.donation.system.model.entity.Donation());
        return "donations/add";
    }

    @PostMapping("/donations/record")
    public String recordDonation(@ModelAttribute com.donation.system.model.entity.Donation donation) {
        donationService.recordDonation(donation);
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
        model.addAttribute("eventCount", donationService.getEventCount());
        return "admin/reports";
    }
}
