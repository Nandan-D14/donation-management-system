package com.donation.system.service.strategy;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Request;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern — Concrete Strategy: Normal (FIFO) Matching.
 * Matches donation bloodType against requestDetails, sorted by createdAt (oldest first).
 * GRASP: Low Coupling — no dependency on Donor, Patient, or Admin.
 *
 * @author Neha (SRN 379)
 */
@Component("normalMatchingStrategy")
public class NormalMatchingStrategy implements MatchingStrategy {

    @Override
    public List<Request> match(Donation donation, List<Request> requests) {
        return requests.stream()
                .filter(req -> donation.getBloodType() != null
                        && req.getRequestDetails() != null
                        && donation.getBloodType().equalsIgnoreCase(req.getRequestDetails()))
                .sorted(Comparator.comparing(Request::getCreatedAt))
                .limit(donation.getQuantity())
                .collect(Collectors.toList());
    }
}
