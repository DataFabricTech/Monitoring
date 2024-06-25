package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ServicesHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesHistoryRepository extends JpaRepository<ServicesHistory, UUID> {
    List<ServicesHistory> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    List<ServicesHistory> findServicesHistoriesByServiceIDOrderByUpdatedAtDesc(UUID serviceID, Pageable pageable);
    List<ServicesHistory> findByServiceIDAndEventInOrderByUpdatedAtDesc(UUID serviceID, List<String> events, Pageable pageable);
}
