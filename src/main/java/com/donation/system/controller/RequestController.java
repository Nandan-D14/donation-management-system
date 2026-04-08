package com.donation.system.controller;

import com.donation.system.model.entity.User;
import com.donation.system.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import com.donation.system.service.RequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public String listRequests(Model model) {
        model.addAttribute("requests", requestService.getAllRequests());
        return "requests/list";
    }

    @GetMapping({"/create", "/add"})
    public String addRequestForm(@RequestParam(required = false) String success,
                                 HttpSession session,
                                 Model model) {
        Object userMail = session.getAttribute("userMail");
        if (userMail == null) {
            return "redirect:/login";
        }
        if ("created".equals(success)) {
            model.addAttribute("success", "Request created successfully.");
        }
        model.addAttribute("currentUserMail", String.valueOf(userMail));
        return "requests/add";
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

        String userMail = String.valueOf(userMailObj).trim().toLowerCase();
        Optional<User> currentUserOptional = userRepository.findByMail(userMail);
        if (currentUserOptional.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        User currentUser = currentUserOptional.get();
        String creatorRole = (currentUser.getRole() == null || currentUser.getRole().isBlank())
                ? "USER"
                : currentUser.getRole();
        String userName = (currentUser.getName() == null || currentUser.getName().isBlank())
                ? "User"
                : currentUser.getName();

        try {
            requestService.createRequest(creatorRole, requestType, userName, userMail, detail, quantity);
            return "redirect:/requests/create?success=created";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("currentUserMail", userMail);
            return "requests/add";
        }
    }
}
