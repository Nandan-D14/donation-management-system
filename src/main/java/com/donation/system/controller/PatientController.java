package com.donation.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.donation.system.model.entity.Patient;
import com.donation.system.service.PatientService;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "patient/dashboard";
    }

    @GetMapping("/request")
    public String requestPage() {
        return "patient/request";
    }

    @PostMapping("/request")
    public String save(@ModelAttribute Patient p) {
        service.registerPatient(p);
        return "redirect:/patient/track";
    }

    @GetMapping("/track")
    public String track(Model model) {
        model.addAttribute("requests", service.getAllRequests());
        return "patient/track";
    }
}