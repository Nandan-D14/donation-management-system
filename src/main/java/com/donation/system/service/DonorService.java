package com.donation.system.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.donation.system.model.entity.Donor;
import com.donation.system.repository.DonorRepository;

@Service
public class DonorService {

    private final DonorRepository repo;

    public DonorService(DonorRepository repo) {
        this.repo = repo;
    }

    public Donor saveDonor(Donor d) {
        d.setAvailability(true);
        d.setStatus("DONATED");
        return repo.save(d);
    }

    public List<Donor> getAll() {
        return repo.findAll();
    }
}
