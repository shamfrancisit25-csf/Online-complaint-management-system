package com.resolve.ocms.repository;
import com.resolve.ocms.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ComplaintRepository extends JpaRepository<Complaint,Long> {
    Optional<Complaint> findById(String id);
}
