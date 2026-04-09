package com.donation.system.service.strategy;

import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.Request;

/**
 * Strategy pattern contract for matching inventory records against requests.
 *
 * @author Team
 */
public interface MatchingStrategy {
    boolean matches(Inventory inventory, Request request);
}
