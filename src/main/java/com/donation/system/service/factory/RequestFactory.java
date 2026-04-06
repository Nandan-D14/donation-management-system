package com.donation.system.service.factory;

import com.donation.system.model.entity.Request;
import com.donation.system.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RequestFactory {

	public Request createBloodRequest(User createdBy, String bloodType, Integer unitsRequested, String notes) {
		String details = unitsRequested + " unit(s) of " + bloodType;
		return new Request(createdBy, "BLOOD", details, notes);
	}

	public Request createOrganRequest(User createdBy, String organType, String notes) {
		String details = "Organ: " + organType;
		return new Request(createdBy, "ORGAN", details, notes);
	}
}
