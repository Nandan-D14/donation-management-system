package com.donation.system.repository;

import com.donation.system.model.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
	List<Request> findByStatusIgnoreCaseOrderByCreatedAtAsc(String status);
	List<Request> findByCreatedBy_MailOrderByCreatedAtDesc(String mail);
}
