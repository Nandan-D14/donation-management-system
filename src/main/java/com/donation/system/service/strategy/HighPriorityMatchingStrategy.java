package com.donation.system.service.strategy;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Request;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern — Concrete Strategy: High Priority Matching.
 * Matches donation bloodType against requestDetails, sorted by ID (highest priority first).
 * GRASP: Low Coupling — no dependency on Donor, Patient, or Admin.
 *
 * @author Neha (SRN 379)
 */
@Component("highPriorityMatchingStrategy")
public class HighPriorityMatchingStrategy implements MatchingStrategy {

    @Override
    public List<Request> match(Donation donation, List<Request> requests) {
        return requests.stream()
                .filter(req -> donation.getDonationType() != null
                        && req.getRequestDetails() != null
                        && donation.getDonationType().equalsIgnoreCase(req.getRequestDetails()))
                .sorted(Comparator.comparing(Request::getId))
                .limit(donation.getQuantity())
                .collect(Collectors.toList());
    }
}
