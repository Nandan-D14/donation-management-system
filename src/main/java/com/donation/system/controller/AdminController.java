package com.donation.system.controller;

import com.donation.system.model.entity.Admin;
import com.donation.system.model.entity.Donation;
import com.donation.system.service.AdminService;
import com.donation.system.service.DonationService;
import com.donation.system.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Admin operations controller.
 *
 * @author Team
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final DonationService donationService;
    private final RequestService requestService;

    public AdminController(AdminService adminService, DonationService donationService, RequestService requestService) {
        this.adminService = adminService;
        this.donationService = donationService;
        this.requestService = requestService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("message", "Welcome to Admin Dashboard");
        model.addAttribute("donationCount", donationService.getAllDonations().size());
        model.addAttribute("adminCount", adminService.getAllAdmins().size());
        model.addAttribute("eventCount", donationService.getEventCount());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin/list";
    }

    @GetMapping("/users/add")
    public String addUserForm(HttpSession session, Model model) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("admin", new Admin());
        return "admin/add";
    }

    @PostMapping("/users/add")
    public String addUser(HttpSession session, @ModelAttribute Admin admin) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        adminService.saveAdmin(admin);
        return "redirect:/admin/users";
    }

    @GetMapping("/donations/add")
    public String recordDonationForm(HttpSession session, Model model) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("donation", new Donation());
        return "donations/add";
    }

    @PostMapping("/donations/add")
    public String recordDonation(HttpSession session, @ModelAttribute Donation donation) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        donationService.recordDonation(donation);
        return "redirect:/admin/donations";
    }

    @PostMapping("/donations/update-status/{id}")
    public String updateDonationStatus(HttpSession session,
                                       @PathVariable int id,
                                       @RequestParam String status) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        donationService.updateStatus(id, status);
        return "redirect:/admin/donations";
    }

    @GetMapping("/donations")
    public String listDonations(HttpSession session, Model model) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("donations", donationService.getAllDonations());
        return "donations/list";
    }

    @GetMapping("/reports")
    public String generateReports(HttpSession session, Model model) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("donations", donationService.getAllDonations());
        model.addAttribute("donationCount", donationService.getDonationCount());
        model.addAttribute("eventCount", donationService.getEventCount());
        return "admin/reports";
    }

    @GetMapping("/requests")
    public String reviewRequests(HttpSession session, Model model) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("requests", requestService.getAllRequests());
        return "admin/requests";
    }

    @PostMapping("/requests/{id}/approve")
    public String approveRequest(HttpSession session, @PathVariable int id) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        requestService.updateRequestStatus(id, "APPROVED");
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/{id}/deny")
    public String denyRequest(HttpSession session, @PathVariable int id) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        requestService.updateRequestStatus(id, "DENIED");
        return "redirect:/admin/requests";
    }
}
