package com.donation.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patient")
public class PatientController {

    // 👉 Dashboard page
    @GetMapping("/dashboard")
    public String dashboard() {
        return "patient/dashboard";
    }

    // 👉 Show request form
    @GetMapping("/request")
    public String requestPage() {
        return "patient/request";
    }

    // 👉 Handle request form
    @PostMapping("/request")
    public String request() {
        // You can add logic later (save to DB)
        return "redirect:/patient/track";
    }

    // 👉 Track request status
    @GetMapping("/track")
    public String track() {
        return "patient/track";
    }

    // 👉 Register patient (optional)
    @PostMapping("/register")
    public String register() {
        return "redirect:/patient/dashboard";
    }
}