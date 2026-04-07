package com.donation.system.service.strategy;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Request;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern — Concrete Strategy: Normal (FIFO) Matching.
 * @author Neha (SRN 379)
 */
@Component("normalMatchingStrategy")
public class NormalMatchingStrategy implements MatchingStrategy {

    @Override
    public List<Request> match(Donation donation, List<Request> pendingRequests) {
        int limit = donation.getQuantity() > 0 ? donation.getQuantity() : 0;

        return pendingRequests.stream()
                .filter(req -> matchesType(donation, req))
                .sorted(Comparator.comparing(Request::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private boolean matchesType(Donation donation, Request request) {
        return donation.getDonationType() != null
                && donation.getDonationType().equalsIgnoreCase(request.getRequestType());
    }
}
