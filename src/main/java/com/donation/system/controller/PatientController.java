package com.donation.system.controller;

import com.donation.system.repository.PatientRepository;
import com.donation.system.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient flow controller.
 *
 * @author Team
 */
@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientRepository patientRepository;
    private final RequestService requestService;

    public PatientController(PatientRepository patientRepository, RequestService requestService) {
        this.patientRepository = patientRepository;
        this.requestService = requestService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("userName", session.getAttribute("userName"));
        return "patient/dashboard";
    }

    @GetMapping("/request")
    public String requestPage(HttpSession session) {
        if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        return "patient/request";
    }

    @PostMapping("/request")
    public String save(HttpSession session,
                       @RequestParam String requestType,
                       @RequestParam String detail,
                       @RequestParam int quantity,
                       Model model) {
        if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }

        String mail = (String) session.getAttribute("userMail");
        patientRepository.findByMail(mail)
                .orElseThrow(() -> new IllegalArgumentException("Patient account not found"));

        try {
            requestService.createRequest("PATIENT", requestType,
                    String.valueOf(session.getAttribute("userName")),
                    mail,
                    detail,
                    quantity);
            return "redirect:/patient/track";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "patient/request";
        }
    }

    @GetMapping("/track")
    public String track(HttpSession session, Model model) {
        if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        String userMail = (String) session.getAttribute("userMail");
        model.addAttribute("requests", requestService.getRequestsForUserMail(userMail));
        return "patient/track";
    }
}
