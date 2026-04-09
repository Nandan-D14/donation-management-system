package com.donation.system.service.strategy;

import com.donation.system.model.entity.BloodRequest;
import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.Request;

/**
 * Matches blood requests to compatible inventory entries.
 *
 * @author Team
 */
public class BloodMatchingStrategy implements MatchingStrategy {

    @Override
    public boolean matches(Inventory inventory, Request request) {
        if (!(request instanceof BloodRequest bloodRequest)) {
            return false;
        }
        if (inventory.getBloodType() == null || bloodRequest.getBloodGroup() == null) {
            return false;
        }
        return inventory.getBloodType().equalsIgnoreCase(bloodRequest.getBloodGroup());
    }
}
