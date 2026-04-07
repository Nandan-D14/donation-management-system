package com.donation.system.service.strategy;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Request;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern — Concrete Strategy: High Priority Matching.
 *
 * Matches donations to requests ordered by urgency level:
 *   CRITICAL (1) → HIGH (2) → NORMAL (3)
 *
 * GRASP: Low Coupling — no dependency on Donor, Patient, Admin, or other modules.
 *
 * @author Neha (SRN 379)
 */
@Component("highPriorityMatchingStrategy")
public class HighPriorityMatchingStrategy implements MatchingStrategy {

    /**
     * Strategy Pattern applied here:
     * Overrides match() to sort by urgency — CRITICAL patients served first.
     */
    @Override
    public List<Request> match(Donation donation, List<Request> pendingRequests) {

        int limit = (donation.getQuantity() != null && donation.getQuantity() > 0)
                ? donation.getQuantity()
                : 0;

        return pendingRequests.stream()
                .filter(req -> matchesType(donation, req))
                .sorted(Comparator.comparingInt(this::urgencyPriority))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Maps urgency level string to a sortable integer.
     * Lower number = higher priority (ascending sort).
     */
    private int urgencyPriority(Request request) {
        if (request.getPatient() == null) return 4;
        String level = request.getPatient().getUrgencyLevel();
        if (level == null) return 4;
        return switch (level.toUpperCase()) {
            case "CRITICAL" -> 1;
            case "HIGH"     -> 2;
            case "NORMAL"   -> 3;
            default         -> 4;
        };
    }

    /**
     * Returns true if the donation type matches the request type (BLOOD or ORGAN).
     */
    private boolean matchesType(Donation donation, Request request) {
        return donation.getDonationType() != null
                && donation.getDonationType().equalsIgnoreCase(request.getRequestType());
    }
}
