package com.donation.system.service;

import com.donation.system.model.entity.Patient;
import com.donation.system.model.entity.Request;
import com.donation.system.model.entity.User;
import com.donation.system.repository.PatientRepository;
import com.donation.system.repository.RequestRepository;
import com.donation.system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public RequestService(RequestRepository requestRepository,
                          UserRepository userRepository,
                          PatientRepository patientRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Request createRequest(String creatorRole,
                                 String requestType,
                                 String userName,
                                 String userMail,
                                 String detail,
                                 int quantity) {
        String normalizedCreatorRole = normalizeRequired(creatorRole, "creatorRole").toUpperCase();
        String normalizedRequestType = normalizeRequired(requestType, "requestType").toUpperCase();
        String normalizedUserName = normalizeRequired(userName, "userName");
        String normalizedUserMail = normalizeRequired(userMail, "userMail").toLowerCase();
        String normalizedDetail = normalizeRequired(detail, "detail");

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        if (!"BLOOD".equals(normalizedRequestType) && !"ORGAN".equals(normalizedRequestType)) {
            throw new IllegalArgumentException("requestType must be BLOOD or ORGAN");
        }

        if ("PATIENT".equals(normalizedCreatorRole)) {
            return createPatientRequest(normalizedRequestType, normalizedUserName, normalizedUserMail, normalizedDetail, quantity);
        }
        if (!"USER".equals(normalizedCreatorRole)) {
            throw new IllegalArgumentException("creatorRole must be USER or PATIENT");
        }
        return createUserRequest(normalizedRequestType, normalizedUserName, normalizedUserMail, normalizedDetail, quantity);
    }

    public Request createRequest(String requestType, String userName, String userMail, String detail, int quantity) {
        return createRequest("USER", requestType, userName, userMail, detail, quantity);
    }

    private Request createUserRequest(String requestType, String userName, String userMail, String detail, int quantity) {
        User creator = userRepository.findByMail(userMail)
                .map(existing -> {
                    existing.setName(userName);
                    return existing;
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(userName);
                    newUser.setMail(userMail);
                    newUser.setRole("REQUESTER");
                    return newUser;
                });

        User savedCreator = userRepository.save(creator);

        Request request;
        if ("ORGAN".equalsIgnoreCase(requestType)) {
            request = savedCreator.createOrganRequest(detail, quantity);
        } else {
            request = savedCreator.createBloodRequest(detail, quantity);
        }

        return requestRepository.save(request);
    }

    private Request createPatientRequest(String requestType, String userName, String userMail, String detail, int quantity) {
        Patient patient = patientRepository.findByMail(userMail).orElseGet(Patient::new);
        patient.setName(userName);
        patient.setMail(userMail);

        User linkedUser = patient.getUserAccount();
        if (linkedUser == null) {
            linkedUser = userRepository.findByMail(userMail).orElseGet(User::new);
        }

        linkedUser.setName(userName);
        linkedUser.setMail(userMail);
        linkedUser.setRole("PATIENT");

        User savedLinkedUser = userRepository.save(linkedUser);
        patient.setUserAccount(savedLinkedUser);
        Patient savedPatient = patientRepository.save(patient);

        Request request;
        if ("ORGAN".equalsIgnoreCase(requestType)) {
            request = savedPatient.createOrganRequest(detail, quantity);
        } else {
            request = savedPatient.createBloodRequest(detail, quantity);
        }

        return requestRepository.save(request);
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }
}
