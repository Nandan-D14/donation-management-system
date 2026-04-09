package com.donation.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Legacy root aliases now delegated to the main auth entry points.
 *
 * @author Team
 */
@Controller
@RequestMapping("/admin-auth")
public class RootController {

    @GetMapping({"", "/"})
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register() {
        return "redirect:/register";
    }
}
