package com.donation.system.controller;

import com.donation.system.model.entity.Request;
import com.donation.system.model.entity.User;
import com.donation.system.repository.RequestRepository;
import com.donation.system.repository.UserRepository;
import com.donation.system.service.factory.RequestFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/requests")
public class RequestController {

	private final RequestRepository requestRepository;
	private final UserRepository userRepository;
	private final RequestFactory requestFactory;

	public RequestController(
			RequestRepository requestRepository,
			UserRepository userRepository,
			RequestFactory requestFactory
	) {
		this.requestRepository = requestRepository;
		this.userRepository = userRepository;
		this.requestFactory = requestFactory;
	}

	@GetMapping
	public String listRequests(Model model) {
		model.addAttribute("requests", requestRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
		return "requests/index";
	}

	@GetMapping("/new")
	public String showNewRequestForm(Model model) {
		if (!model.containsAttribute("requestForm")) {
			model.addAttribute("requestForm", new RequestForm());
		}
		return "requests/new";
	}

	@PostMapping
	public String createRequest(@ModelAttribute("requestForm") RequestForm form, RedirectAttributes redirectAttributes) {
		if (form.name == null || form.name.isBlank() || form.email == null || form.email.isBlank()) {
			redirectAttributes.addFlashAttribute("error", "Name and email are required.");
			redirectAttributes.addFlashAttribute("requestForm", form);
			return "redirect:/requests/new";
		}

		User user = userRepository.findByEmailIgnoreCase(form.email.trim())
				.orElseGet(() -> userRepository.save(new User(form.name.trim(), form.email.trim().toLowerCase())));

		Request request;
		if ("ORGAN".equalsIgnoreCase(form.requestType)) {
			request = user.createOrganRequest(requestFactory, form.organType, form.notes);
		} else {
			request = user.createBloodRequest(requestFactory, form.bloodType, form.unitsRequested == null ? 1 : form.unitsRequested, form.notes);
		}

		requestRepository.save(request);
		redirectAttributes.addFlashAttribute("message", "Request created.");
		return "redirect:/requests";
	}

	public static class RequestForm {
		private String requestType = "BLOOD";
		private String name;
		private String email;
		private String bloodType;
		private Integer unitsRequested = 1;
		private String organType;
		private String notes;

		public String getRequestType() {
			return requestType;
		}

		public void setRequestType(String requestType) {
			this.requestType = requestType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getBloodType() {
			return bloodType;
		}

		public void setBloodType(String bloodType) {
			this.bloodType = bloodType;
		}

		public Integer getUnitsRequested() {
			return unitsRequested;
		}

		public void setUnitsRequested(Integer unitsRequested) {
			this.unitsRequested = unitsRequested;
		}

		public String getOrganType() {
			return organType;
		}

		public void setOrganType(String organType) {
			this.organType = organType;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}
	}
}
