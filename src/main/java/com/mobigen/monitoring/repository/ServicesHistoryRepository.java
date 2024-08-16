package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.HistoryDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesHistoryRepository extends JpaRepository<HistoryDTO, UUID> {
    List<HistoryDTO> findAllByOrderByUpdateAtDesc(Pageable pageable);

    List<HistoryDTO> findServicesHistoriesByServiceIDOrderByUpdateAtDesc(UUID serviceID, Pageable pageable);

    List<HistoryDTO> findByServiceIDAndDescriptionInOrderByUpdateAtDesc(UUID serviceID, List<String> descriptions, Pageable pageable);
}
