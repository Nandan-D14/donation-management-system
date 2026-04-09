package com.donation.system.service;

import com.donation.system.model.entity.Request;
import com.donation.system.model.entity.User;
import com.donation.system.repository.RequestRepository;
import com.donation.system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Request creation and listing service.
 *
 * @author Team
 */
@Service
public class RequestService {

    private static final EnumSet<RequestStatus> TERMINAL_STATUSES = EnumSet.of(RequestStatus.APPROVED, RequestStatus.DENIED);

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public RequestService(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<Request> getPendingRequests() {
        return requestRepository.findByStatusIgnoreCaseOrderByCreatedAtAsc("PENDING");
    }

    public List<Request> getRequestsForUserMail(String userMail) {
        String normalizedMail = normalizeRequired(userMail, "userMail").toLowerCase();
        return requestRepository.findByCreatedBy_MailOrderByCreatedAtDesc(normalizedMail);
    }

    public Optional<Request> getRequestById(int id) {
        return requestRepository.findById(id);
    }

    public List<String> getAllowedNextStatuses(String currentStatus) {
        RequestStatus current = parseStatus(currentStatus);
        return Arrays.stream(RequestStatus.values())
                .filter(next -> next != RequestStatus.PENDING)
                .filter(next -> isValidTransition(current, next))
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public boolean updateRequestStatus(int id, String status) {
        Optional<Request> requestOptional = requestRepository.findById(id);
        if (requestOptional.isEmpty()) {
            return false;
        }

        RequestStatus nextStatus = parseStatus(status);
        Request request = requestOptional.get();
        RequestStatus currentStatus = parseStatus(request.getStatus());

        if (!isValidTransition(currentStatus, nextStatus)) {
            throw new IllegalArgumentException(
                    "Invalid request status transition: " + currentStatus + " -> " + nextStatus);
        }

        request.setStatus(nextStatus.name());
        requestRepository.save(request);
        return true;
    }

    public Request createRequest(String creatorRole,
                                 String requestType,
                                 String userName,
                                 String userMail,
                                 String detail,
                                 int quantity) {
        String normalizedRole = normalizeRequired(creatorRole, "creatorRole").toUpperCase();
        String normalizedType = normalizeRequired(requestType, "requestType").toUpperCase();
        String normalizedName = normalizeRequired(userName, "userName");
        String normalizedMail = normalizeRequired(userMail, "userMail").toLowerCase();
        String normalizedDetail = normalizeRequired(detail, "detail");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (!"BLOOD".equals(normalizedType) && !"ORGAN".equals(normalizedType)) {
            throw new IllegalArgumentException("Request type must be BLOOD or ORGAN.");
        }

        User creator = userRepository.findByMail(normalizedMail)
                .orElseThrow(() -> new IllegalArgumentException("Logged in user not found."));

        creator.setName(normalizedName);
        if (creator.getRole() == null || creator.getRole().isBlank()) {
            creator.setRole(normalizedRole);
        }
        User savedCreator = userRepository.save(creator);

        Request request = "ORGAN".equals(normalizedType)
                ? savedCreator.createOrganRequest(normalizedDetail, quantity)
                : savedCreator.createBloodRequest(normalizedDetail, quantity);

        return requestRepository.save(request);
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private RequestStatus parseStatus(String status) {
        String normalized = normalizeRequired(status, "status").toUpperCase();
        try {
            return RequestStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Status must be one of: PENDING, APPROVED, DENIED.");
        }
    }

    private boolean isValidTransition(RequestStatus current, RequestStatus next) {
        if (current == next) {
            return true;
        }

        if (TERMINAL_STATUSES.contains(current)) {
            return false;
        }

        return switch (current) {
            case PENDING -> next == RequestStatus.APPROVED || next == RequestStatus.DENIED;
            case APPROVED, DENIED -> false;
        };
    }

    private enum RequestStatus {
        PENDING,
        APPROVED,
        DENIED
    }
}
