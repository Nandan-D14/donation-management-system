package com.donation.system.controller;

import com.donation.system.model.entity.User;
import com.donation.system.repository.UserRepository;
import com.donation.system.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Request module controller.
 *
 * @author Team
 */
@Controller
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserRepository userRepository;

    public RequestController(RequestService requestService, UserRepository userRepository) {
        this.requestService = requestService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listRedirect() {
        return "redirect:/requests/list";
    }

    @GetMapping("/list")
    public String listRequests(HttpSession session, Model model) {
        if (session.getAttribute("userMail") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/patient/track";
        }
        model.addAttribute("requests", requestService.getAllRequests());
        return "requests/list";
    }

    @GetMapping({"/create", "/add"})
    public String addRequestForm(HttpSession session, Model model) {
        Object userMail = session.getAttribute("userMail");
        if (userMail == null) {
            return "redirect:/login";
        }
        if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/requests/list";
        }
        return "redirect:/patient/request";
    }

    @PostMapping({"/create", "/add"})
    public String createRequest(HttpSession session,
                                @RequestParam String requestType,
                                @RequestParam String detail,
                                @RequestParam int quantity,
                                Model model) {
        Object userMailObj = session.getAttribute("userMail");
        if (userMailObj == null) {
            return "redirect:/login";
        }
        if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/requests/list";
        }

        String userMail = String.valueOf(userMailObj).trim().toLowerCase();
        Optional<User> currentUserOptional = userRepository.findByMail(userMail);
        if (currentUserOptional.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        User currentUser = currentUserOptional.get();
        String creatorRole = currentUser.getRole() == null ? "USER" : currentUser.getRole();
        String userName = currentUser.getName() == null ? "User" : currentUser.getName();

        try {
            requestService.createRequest(creatorRole, requestType, userName, userMail, detail, quantity);
            return "redirect:/patient/track";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("currentUserMail", userMail);
            return "patient/request";
        }
    }

    @GetMapping("/{id}")
    public String viewRequestById(HttpSession session, @PathVariable int id, Model model) {
        if (session.getAttribute("userMail") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/requests/list";
        }
        return requestService.getRequestById(id)
                .map(req -> {
                    model.addAttribute("request", req);
                    model.addAttribute("allowedStatuses", requestService.getAllowedNextStatuses(req.getStatus()));
                    return "requests/status";
                })
                .orElse("redirect:/requests/list");
    }

    @PostMapping("/{id}/status")
    public String updateRequestStatus(HttpSession session,
                                      @PathVariable int id,
                                      @RequestParam String status,
                                      RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userMail") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/requests/list";
        }
        try {
            requestService.updateRequestStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Request status updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/requests/" + id;
    }
}
