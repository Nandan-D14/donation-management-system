package com.donation.system.controller;

import com.donation.system.model.entity.Inventory;
import com.donation.system.service.InventoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Handles inventory pages for admin users.
 *
 * @author Team
 */
@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping({"", "/dashboard"})
    public String inventoryDashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/login";
        }
        // Inventory is read-only, auto-synced from donations
        model.addAttribute("inventoryItems", inventoryService.getAllInventory());
        return "inventory/dashboard";
    }

    @GetMapping("/stock")
    public String stock(HttpSession session,
                        @RequestParam(required = false) String message,
                        @RequestParam(required = false) String error,
                        Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/login";
        }

        List<Inventory> inventoryItems = inventoryService.getAllInventory();
        model.addAttribute("inventoryItems", inventoryItems);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "inventory/stock";
    }

    @PostMapping("/stock/add")
    public String addStock(HttpSession session,
                           @RequestParam(required = false) String bloodType,
                           @RequestParam(required = false) String organType,
                           @RequestParam int amount) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/login";
        }

        try {
            inventoryService.addStock(bloodType, organType, amount);
            return "redirect:/inventory/stock?message=Stock added successfully";
        } catch (IllegalArgumentException ex) {
            return "redirect:/inventory/stock?error=" + ex.getMessage();
        }
    }

    @PostMapping("/stock/reduce")
    public String reduceStock(HttpSession session,
                              @RequestParam(required = false) String bloodType,
                              @RequestParam(required = false) String organType,
                              @RequestParam int amount) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/login";
        }

        boolean reduced = inventoryService.reduceStock(bloodType, organType, amount);
        if (reduced) {
            return "redirect:/inventory/stock?message=Stock reduced successfully";
        }
        return "redirect:/inventory/stock?error=Unable to reduce stock";
    }

    @GetMapping("/matching")
    public String matching(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/login";
        }

        model.addAttribute("strategy", inventoryService.getMatchingStrategy());
        model.addAttribute("requests", inventoryService.getRequestMatchingPreview());
        model.addAttribute("inventoryItems", inventoryService.getAllInventory());
        return "inventory/matching";
    }

    @PostMapping("/matching/strategy")
    public String switchStrategy(HttpSession session, @RequestParam String strategy) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/login";
        }

        inventoryService.switchMatchingStrategy(strategy);
        return "redirect:/inventory/matching";
    }

}
