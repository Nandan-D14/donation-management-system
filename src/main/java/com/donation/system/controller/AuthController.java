package com.donation.system.controller;

import com.donation.system.model.entity.User;
import com.donation.system.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) { this.userRepository = userRepository; }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String registered,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid credentials");
        if (registered != null) model.addAttribute("success", "Registration successful. Please login.");
        return "auth/login";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String mail,
                           @RequestParam String password,
                           @RequestParam(required = false) Integer phone,
                           Model model) {
        String m = mail.trim().toLowerCase();
        String p = password == null ? "" : password.trim();
        if (p.isBlank()) {
            model.addAttribute("error", "Password is required");
            return "auth/register";
        }

        Optional<User> existing = userRepository.findByMail(m);
        if (existing.isPresent()) {
            User current = existing.get();

            // For demo: allow register to complete/reset credentials for existing email.
            current.setName(name.trim());
            current.setPassword(p);
            current.setPhone(phone);
            if (current.getRole() == null || current.getRole().isBlank()) {
                current.setRole("USER");
            }
            current.register();
            userRepository.save(current);
            return "redirect:/login?registered=1";
        }
        User u = new User();
        u.setName(name.trim()); u.setMail(m); u.setPassword(p); u.setPhone(phone); u.setRole("USER");
        u.register(); userRepository.save(u);
        return "redirect:/login?registered=1";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String mail, @RequestParam String password, HttpSession session) {
        String m = mail.trim().toLowerCase();
        String p = password == null ? "" : password.trim();
        Optional<User> u = userRepository.findByMail(m);
        if (u.isEmpty() || !u.get().login(m, p)) return "redirect:/login?error=1";
        session.setAttribute("userMail", m);
        return "redirect:/request";
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
