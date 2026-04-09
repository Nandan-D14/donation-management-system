package com.donation.system.controller;

import com.donation.system.model.entity.Admin;
import com.donation.system.model.entity.Donor;
import com.donation.system.model.entity.Patient;
import com.donation.system.model.entity.User;
import com.donation.system.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Handles user registration and login.
 *
 * @author Team
 */
@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String registered,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid credentials");
        }
        if (registered != null) {
            model.addAttribute("success", "Registration successful. Please login.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String mail,
                           @RequestParam String password,
                           @RequestParam(required = false) Long phone,
                           @RequestParam(defaultValue = "USER") String role,
                           Model model) {
        String normalizedMail = mail.trim().toLowerCase();
        String normalizedRole = role.trim().toUpperCase();

        if (userRepository.findByMail(normalizedMail).isPresent()) {
            model.addAttribute("error", "Email already registered");
            return "auth/register";
        }

        User user;
        switch (normalizedRole) {
            case "DONOR" -> user = new Donor();
            case "PATIENT" -> user = new Patient();
            case "ADMIN" -> user = new Admin();
            default -> user = new Patient();
        }

        user.setName(name.trim());
        user.setMail(normalizedMail);
        user.setPassword(password.trim());
        user.setPhone(phone);
        user.setRole(normalizedRole);
        user.register();
        userRepository.save(user);
        return "redirect:/login?registered=1";
    }

    @PostMapping("/login")
    public String login(@RequestParam String mail,
                        @RequestParam String password,
                        HttpSession session) {
        String normalizedMail = mail.trim().toLowerCase();

        Optional<User> optionalUser = userRepository.findByMailAndPassword(normalizedMail, password.trim());
        if (optionalUser.isEmpty()) {
            return "redirect:/login?error=1";
        }

        User user = optionalUser.get();
        session.setAttribute("userMail", user.getMail());
        session.setAttribute("userName", user.getName());
        session.setAttribute("role", user.getRole());

        return switch (user.getRole().toUpperCase()) {
            case "DONOR" -> "redirect:/donor/dashboard";
            case "PATIENT" -> "redirect:/patient/dashboard";
            case "ADMIN" -> "redirect:/admin/dashboard";
            default -> "redirect:/requests/create";
        };
    }

    @GetMapping("/request")
    public String request(HttpSession session) {
        return session.getAttribute("userMail") == null ? "redirect:/login" : "redirect:/requests/create";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
