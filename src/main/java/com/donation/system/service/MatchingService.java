package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Request;
import com.donation.system.service.strategy.MatchingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Strategy Pattern Context — delegates matching to the active MatchingStrategy.
 * Strategy can be swapped at runtime via setStrategy().
 * GRASP: Low Coupling — depends only on the MatchingStrategy interface.
 *
 * @author Neha (SRN 379)
 */
@Service
public class MatchingService {

    private MatchingStrategy strategy;

    public MatchingService(@Qualifier("highPriorityMatchingStrategy") MatchingStrategy defaultStrategy) {
        this.strategy = defaultStrategy;
    }

    public void setStrategy(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Request> match(Donation donation, List<Request> requests) {
        return strategy.match(donation, requests);
    }
}
