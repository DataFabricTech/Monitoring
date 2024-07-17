package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ServicesConnect;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesConnectRepository extends JpaRepository<ServicesConnect, UUID> {
    List<ServicesConnect> findByOrderByQueryExecutionTimeDesc(Pageable pageable);
    List<ServicesConnect> findByOrderByQueryExecutionTimeAsc(Pageable pageable);
    List<ServicesConnect> findByServiceIDOrderByExecuteAtDesc(UUID serviceID, Pageable pageable);
}
