package com.donation.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/donor")
public class DonorController {

    // 👉 Dashboard page
    @GetMapping("/dashboard")
    public String dashboard() {
        return "donor/dashboard";
    }

    // 👉 Show donate form
    @GetMapping("/donate")
    public String donatePage() {
        return "donor/donate";
    }

    // 👉 Handle donation form submission
    @PostMapping("/donate")
    public String donate() {
        // You can add logic later (save to DB)
        return "redirect:/donor/status";
    }

    // 👉 Show donation status
    @GetMapping("/status")
    public String status() {
        return "donor/status";
    }

    // 👉 Register donor (optional)
    @PostMapping("/register")
    public String register() {
        return "redirect:/donor/dashboard";
    }
}