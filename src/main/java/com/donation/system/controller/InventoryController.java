package com.donation.system.controller;

import com.donation.system.model.entity.Inventory;
import com.donation.system.service.InventoryService;
import com.donation.system.service.strategy.MatchingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * GRASP: Low Coupling — Controller only receives HTTP events and delegates to InventoryService.
 * No business logic here. All calculations and data operations are in InventoryService.
 *
 * @author Neha (SRN 379)
 */
@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final MatchingStrategy highPriorityStrategy;
    private final MatchingStrategy normalStrategy;

    public InventoryController(InventoryService inventoryService,
                                @Qualifier("highPriorityMatchingStrategy") MatchingStrategy highPriorityStrategy,
                                @Qualifier("normalMatchingStrategy") MatchingStrategy normalStrategy) {
        this.inventoryService = inventoryService;
        this.highPriorityStrategy = highPriorityStrategy;
        this.normalStrategy = normalStrategy;
    }

    /** GET /inventory/dashboard — show stock overview */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Inventory> allStock = inventoryService.getAllInventory();
        List<Inventory> lowStock = inventoryService.getLowStock(5);
        model.addAttribute("allStock", allStock);
        model.addAttribute("lowStock", lowStock);
        return "inventory/dashboard";
    }

    /** GET /inventory/stock — show stock management page */
    @GetMapping("/stock")
    public String stockPage(Model model) {
        model.addAttribute("inventory", inventoryService.getAllInventory());
        return "inventory/stock";
    }

    /** POST /inventory/stock/add — add stock for a type */
    @PostMapping("/stock/add")
    public String addStock(@RequestParam String bloodType,
                           @RequestParam String organType,
                           @RequestParam int amount,
                           RedirectAttributes redirectAttributes) {
        inventoryService.addStock(bloodType, organType, amount);   // delegates to service
        redirectAttributes.addFlashAttribute("success", "Stock added successfully!");
        return "redirect:/inventory/stock";
    }

    /** POST /inventory/stock/reduce — reduce stock for a type */
    @PostMapping("/stock/reduce")
    public String reduceStock(@RequestParam String bloodType,
                              @RequestParam String organType,
                              @RequestParam int amount,
                              RedirectAttributes redirectAttributes) {
        boolean ok = inventoryService.reduceStock(bloodType, organType, amount);  // delegates
        if (ok) {
            redirectAttributes.addFlashAttribute("success", "Stock reduced successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Insufficient stock or record not found.");
        }
        return "redirect:/inventory/stock";
    }

    /** GET /inventory/matching — view donation-request matching page */
    @GetMapping("/matching")
    public String matchingPage(Model model) {
        model.addAttribute("inventory", inventoryService.getAllInventory());
        return "inventory/matching";
    }

    /** POST /inventory/matching/strategy — switch active matching strategy at runtime */
    @PostMapping("/matching/strategy")
    public String switchStrategy(@RequestParam String strategyType,
                                 RedirectAttributes redirectAttributes) {
        // Strategy Pattern: swap at runtime based on user selection
        if ("HIGH_PRIORITY".equalsIgnoreCase(strategyType)) {
            inventoryService.setStrategy(highPriorityStrategy);
            redirectAttributes.addFlashAttribute("success", "Switched to High Priority strategy (CRITICAL first).");
        } else {
            inventoryService.setStrategy(normalStrategy);
            redirectAttributes.addFlashAttribute("success", "Switched to Normal strategy (FIFO order).");
        }
        return "redirect:/inventory/matching";
    }
}
