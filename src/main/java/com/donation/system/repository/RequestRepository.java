package com.donation.system.repository;

import com.donation.system.model.entity.Request;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {

	List<Request> findByCreatedByIdOrderByCreatedAtDesc(Long createdById);
}
