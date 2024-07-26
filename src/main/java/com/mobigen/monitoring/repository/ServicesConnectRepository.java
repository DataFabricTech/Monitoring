package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ConnectDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesConnectRepository extends JpaRepository<ConnectDTO, UUID> {
    List<ConnectDTO> findByOrderByQueryExecutionTimeDesc(Pageable pageable);
    List<ConnectDTO> findByOrderByQueryExecutionTimeAsc(Pageable pageable);
    List<ConnectDTO> findByServiceIDOrderByExecuteAtDesc(UUID serviceID, Pageable pageable);
}
