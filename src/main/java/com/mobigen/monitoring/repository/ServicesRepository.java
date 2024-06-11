package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.dto.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<Services, UUID> {
    Long countByConnectionStatusIsTrue();
    Services findServicesByServiceID(UUID serviceID);
}
