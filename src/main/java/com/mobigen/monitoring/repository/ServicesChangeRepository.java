package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.dto.ServicesChange;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesChangeRepository extends JpaRepository<ServicesChange, Long> {
    List<ServicesChange> findTopByOrderByUpdatedAtDesc(Pageable pageable);
    List<ServicesChange> findTopByServiceIDOrderByUpdatedAtDesc(UUID serviceId, Pageable pageable);
}
