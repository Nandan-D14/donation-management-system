package com.donation.system.service;

import com.donation.system.model.entity.Admin;
import com.donation.system.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AdminService.
 *
 * @author Team
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    private Admin admin;

    @BeforeEach
    void setup() {
        admin = new Admin();
        admin.setId(1);
        admin.setName("Admin User");
        admin.setMail("admin@example.com");
        admin.setPassword("admin123");
    }

    @Test
    void saveAdmin_shouldSetAdminRole() {
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Admin saved = adminService.saveAdmin(admin);

        assertNotNull(saved);
        assertEquals("ADMIN", saved.getRole());
        verify(adminRepository).save(admin);
    }

    @Test
    void authenticate_shouldReturnTrueForValidCredentials() {
        when(adminRepository.findByMail("admin@example.com")).thenReturn(Optional.of(admin));

        boolean result = adminService.authenticate("admin@example.com", "admin123");

        assertTrue(result);
    }

    @Test
    void authenticate_shouldReturnFalseForInvalidPassword() {
        when(adminRepository.findByMail("admin@example.com")).thenReturn(Optional.of(admin));

        boolean result = adminService.authenticate("admin@example.com", "wrongpass");

        assertFalse(result);
    }

    @Test
    void getAdminByMail_shouldReturnAdmin() {
        when(adminRepository.findByMail("admin@example.com")).thenReturn(Optional.of(admin));

        Optional<Admin> result = adminService.getAdminByMail("admin@example.com");

        assertTrue(result.isPresent());
        assertEquals(admin, result.get());
    }

    @Test
    void getAllAdmins_shouldReturnAdminList() {
        Admin admin2 = new Admin();
        admin2.setName("Another Admin");
        when(adminRepository.findAll()).thenReturn(List.of(admin, admin2));

        List<Admin> admins = adminService.getAllAdmins();

        assertEquals(2, admins.size());
    }
}
