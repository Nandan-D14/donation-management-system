package com.donation.system.service.strategy;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Request;

import java.util.List;

/**
 * Strategy Pattern — MatchingStrategy Interface.
 *
 * Defines the contract for donation-to-request matching algorithms.
 * Concrete implementations: HighPriorityMatchingStrategy, NormalMatchingStrategy.
 *
 * GRASP: Low Coupling — InventoryService depends on THIS interface, not on
 * concrete strategy classes. Strategies can be swapped without changing the service.
 *
 * @author Neha (SRN 379)
 */
public interface MatchingStrategy {

    /**
     * Match a donation to a list of pending requests using this strategy's algorithm.
     *
     * @param donation        the incoming donation to match
     * @param pendingRequests list of all pending requests to consider
     * @return ordered list of matched requests (up to donation quantity)
     */
    List<Request> match(Donation donation, List<Request> pendingRequests);
}
