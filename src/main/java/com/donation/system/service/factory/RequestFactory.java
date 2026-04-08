package com.donation.system.service.factory;

import com.donation.system.model.entity.BloodRequest;
import com.donation.system.model.entity.OrganRequest;
import com.donation.system.model.entity.Request;
import com.donation.system.model.entity.User;

/**
 * Factory Pattern: centralizes creation of BloodRequest and OrganRequest objects.
 */
public final class RequestFactory {

    private RequestFactory() {
    }

    public static Request createBloodRequest(User createdBy, String bloodGroup, int quantity) {
        return new BloodRequest(createdBy, quantity, bloodGroup);
    }

    public static Request createOrganRequest(User createdBy, String organType, int quantity) {
        return new OrganRequest(createdBy, quantity, organType);
    }
}
