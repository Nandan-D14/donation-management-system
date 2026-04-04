package com.donation.system.controller;

import com.donation.system.service.RequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * GRASP Controller: RequestController accepts request module HTTP inputs
 * and delegates request creation/listing to RequestService.
 *
 * @author Sharath (SRN 823)
 */
@Controller
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public String listRequests(Model model) {
        model.addAttribute("requests", requestService.getAllRequests());
        return "requests/list";
    }

    @GetMapping("/add")
    public String addRequestForm(@RequestParam(required = false) String success, Model model) {
        if ("created".equals(success)) {
            model.addAttribute("success", "Request created successfully.");
        }
        return "requests/add";
    }

    @PostMapping("/add")
    public String createRequest(@RequestParam String userName,
                                @RequestParam String userMail,
                                @RequestParam(defaultValue = "USER") String creatorRole,
                                @RequestParam String requestType,
                                @RequestParam String detail,
                                @RequestParam int quantity,
                                Model model) {

        try {
            requestService.createRequest(creatorRole, requestType, userName, userMail, detail, quantity);
            return "redirect:/requests/add?success=created";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "requests/add";
        }
    }
}
