package com.donation.system.controller;

import com.donation.system.model.entity.Admin;
import com.donation.system.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles authentication pages and root redirection.
 */
@Controller
@RequestMapping("/admin-auth")
public class RootController {

    private final AdminService adminService;

    public RootController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping({"", "/"})
    public String root() {
        return "redirect:/admin-auth/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String success) {
        if (!model.containsAttribute("loginAdmin")) {
            model.addAttribute("loginAdmin", new Admin());
        }
        if ("registered".equals(success)) {
            model.addAttribute("success", "Registration successful. Please login.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginAdmin") Admin loginAdmin, Model model) {
        boolean validUser = adminService.authenticate(loginAdmin.getMail(), loginAdmin.getPassword());
        if (validUser) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", "Invalid email or password.");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registerAdmin")) {
            model.addAttribute("registerAdmin", new Admin());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registerAdmin") Admin registerAdmin, Model model) {
        if (registerAdmin.getMail() == null || registerAdmin.getMail().isBlank()) {
            model.addAttribute("registerError", "Email is required.");
            return "register";
        }

        if (adminService.isMailRegistered(registerAdmin.getMail())) {
            model.addAttribute("registerError", "Email already registered. Please login.");
            return "register";
        }

        adminService.saveAdmin(registerAdmin);
        return "redirect:/admin-auth/login?success=registered";
    }
}
