package com.donation.system.controller;

import com.donation.system.model.entity.Patient;
import com.donation.system.model.entity.User;
import com.donation.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for AuthController login/register flows.
 *
 * @author Team
 */
class AuthControllerTest {

    private MockMvc mockMvc;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        AuthController controller = new AuthController(userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void login_shouldRedirectToPatientDashboard() throws Exception {
        User patient = new Patient();
        patient.setMail("patient@example.com");
        patient.setPassword("1234");
        patient.setName("Patient User");
        patient.setRole("PATIENT");

        when(userRepository.findByMailAndPassword(eq("patient@example.com"), eq("1234")))
                .thenReturn(Optional.of(patient));

        mockMvc.perform(post("/login")
                        .param("mail", "patient@example.com")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patient/dashboard"));
    }

    @Test
    void register_shouldRedirectToLoginOnSuccess() throws Exception {
        when(userRepository.findByMail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/register")
                        .param("name", "New User")
                        .param("mail", "new@example.com")
                        .param("password", "pass")
                        .param("role", "PATIENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=1"));
    }
}
