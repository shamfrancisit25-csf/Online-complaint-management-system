package com.resolve.ocms.repository;
import com.resolve.ocms.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ComplaintRepository extends JpaRepository<Complaint,String> {}
