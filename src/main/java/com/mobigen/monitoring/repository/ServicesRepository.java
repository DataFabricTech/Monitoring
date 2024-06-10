package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.dto.Services;
import com.mobigen.monitoring.dto.ServicesKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<Services, ServicesKey> {
    List<Services> findTopByOrderByUpdatedAtDesc(int count);
}
