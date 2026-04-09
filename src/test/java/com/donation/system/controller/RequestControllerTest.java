package com.donation.system.controller;

import com.donation.system.model.entity.Patient;
import com.donation.system.model.entity.Request;
import com.donation.system.repository.UserRepository;
import com.donation.system.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Unit tests for RequestController.
 *
 * @author Team
 */
class RequestControllerTest {

    private MockMvc mockMvc;
    private RequestService requestService;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        requestService = Mockito.mock(RequestService.class);
        userRepository = Mockito.mock(UserRepository.class);
        RequestController controller = new RequestController(requestService, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listRequests_shouldRedirectWhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/requests/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void listRequests_shouldReturnListViewWhenLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", "patient@example.com");
        session.setAttribute("role", "ADMIN");

        when(requestService.getAllRequests()).thenReturn(List.<Request>of());

        mockMvc.perform(get("/requests/list").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("requests/list"))
                .andExpect(model().attributeExists("requests"));
    }

    @Test
    void createRequest_shouldRedirectToListOnSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", "patient@example.com");
        session.setAttribute("role", "PATIENT");

        Patient patient = new Patient();
        patient.setMail("patient@example.com");
        patient.setName("Patient");
        patient.setRole("PATIENT");

        when(userRepository.findByMail("patient@example.com")).thenReturn(Optional.of(patient));
        when(requestService.createRequest(eq("PATIENT"), eq("BLOOD"), eq("Patient"), eq("patient@example.com"), eq("A+"), anyInt()))
                .thenReturn(null);

        mockMvc.perform(post("/requests/create")
                        .session(session)
                        .param("requestType", "BLOOD")
                        .param("detail", "A+")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patient/track"));
    }

    @Test
    void viewRequestById_shouldRedirectNonAdminToList() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", "patient@example.com");
        session.setAttribute("role", "PATIENT");

        mockMvc.perform(get("/requests/1").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/requests/list"));
    }

    @Test
    void updateRequestStatus_shouldRedirectNonAdminToList() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", "patient@example.com");
        session.setAttribute("role", "PATIENT");

        mockMvc.perform(post("/requests/1/status")
                        .session(session)
                        .param("status", "MATCHED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/requests/list"));
    }

    @Test
    void updateRequestStatus_shouldAllowAdminToApprove() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", "admin@example.com");
        session.setAttribute("role", "ADMIN");

        when(requestService.updateRequestStatus(1, "APPROVED")).thenReturn(true);

        mockMvc.perform(post("/requests/1/status")
                        .session(session)
                        .param("status", "APPROVED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/requests/1"));
    }

    @Test
    void listRequests_shouldRedirectPatientToTrack() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userMail", "patient@example.com");
        session.setAttribute("role", "PATIENT");

        mockMvc.perform(get("/requests/list").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patient/track"));
    }
}
