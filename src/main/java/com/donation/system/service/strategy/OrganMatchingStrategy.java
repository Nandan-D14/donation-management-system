package com.donation.system.service.strategy;

import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.OrganRequest;
import com.donation.system.model.entity.Request;

/**
 * Matches organ requests to compatible inventory entries.
 *
 * @author Team
 */
public class OrganMatchingStrategy implements MatchingStrategy {

    @Override
    public boolean matches(Inventory inventory, Request request) {
        if (!(request instanceof OrganRequest organRequest)) {
            return false;
        }
        if (inventory.getOrganType() == null || organRequest.getOrganType() == null) {
            return false;
        }
        return inventory.getOrganType().equalsIgnoreCase(organRequest.getOrganType());
    }
}
