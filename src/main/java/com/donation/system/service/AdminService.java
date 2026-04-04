package com.donation.system.service;

import com.donation.system.model.entity.Admin;
import com.donation.system.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * AdminService handles admin-related data access and persistence.
 *
 * @author Nandan (SRN 363)
 */
@Service
public class AdminService {

    private final AdminRepository adminRepo;

    public AdminService(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    public List<Admin> getAllAdmins() {
        return adminRepo.findAll();
    }

    public Optional<Admin> getAdminById(int id) {
        return adminRepo.findById(id);
    }

    public Optional<Admin> getAdminByMail(String mail) {
        return adminRepo.findByMail(mail);
    }

    public Admin saveAdmin(Admin admin) {
        return adminRepo.save(admin);
    }

    public boolean isMailRegistered(String mail) {
        return adminRepo.findByMail(mail).isPresent();
    }

    public boolean authenticate(String mail, String password) {
        return adminRepo.findByMail(mail)
                .map(admin -> admin.getPassword() != null && admin.getPassword().equals(password))
                .orElse(false);
    }
}
