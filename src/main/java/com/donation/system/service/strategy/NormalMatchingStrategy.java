package com.donation.system.service.strategy;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Request;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern — Concrete Strategy: Normal (FIFO) Matching.
 *
 * Matches donations to requests in first-come-first-served order,
 * sorted by requestDate ascending.
 *
 * GRASP: Low Coupling — no dependency on Donor, Patient, Admin, or other modules.
 *
 * @author Neha (SRN 379)
 */
@Component("normalMatchingStrategy")
public class NormalMatchingStrategy implements MatchingStrategy {

    /**
     * Strategy Pattern applied here:
     * Overrides match() to sort by requestDate — oldest request fulfilled first (FIFO).
     */
    @Override
    public List<Request> match(Donation donation, List<Request> pendingRequests) {

        int limit = (donation.getQuantity() != null && donation.getQuantity() > 0)
                ? donation.getQuantity()
                : 0;

        return pendingRequests.stream()
                .filter(req -> matchesType(donation, req))
                .sorted(Comparator.comparing(Request::getRequestDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Returns true if the donation type matches the request type (BLOOD or ORGAN).
     */
    private boolean matchesType(Donation donation, Request request) {
        return donation.getDonationType() != null
                && donation.getDonationType().equalsIgnoreCase(request.getRequestType());
    }
}
