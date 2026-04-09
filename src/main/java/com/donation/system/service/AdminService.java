package com.donation.system.service;

import com.donation.system.model.entity.Admin;
import com.donation.system.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Admin data access service.
 *
 * @author Team
 */
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminByMail(String mail) {
        return adminRepository.findByMail(mail);
    }

    public Admin saveAdmin(Admin admin) {
        admin.setRole("ADMIN");
        return adminRepository.save(admin);
    }

    public boolean authenticate(String mail, String password) {
        return adminRepository.findByMail(mail)
                .map(admin -> password != null && password.equals(admin.getPassword()))
                .orElse(false);
    }
}
