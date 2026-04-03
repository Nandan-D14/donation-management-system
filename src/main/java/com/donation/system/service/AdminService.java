package com.donation.system.service;

import com.donation.system.model.entity.Admin;
import com.donation.system.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * GRASP: Controller Principle
 * Admin acts as the system controller — coordinates all operations
 * between Donation, User, and Inventory modules without doing the work itself.
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

    public Admin saveAdmin(Admin admin) {
        return adminRepo.save(admin);
    }
}
