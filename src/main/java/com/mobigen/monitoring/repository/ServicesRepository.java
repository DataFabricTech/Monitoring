package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<Services, UUID> {
    Long countByConnectionStatusIsTrue();

    Services findServicesByEntityID(UUID entityID);

    Services findServicesByNameAndConnectionStatusIsFalse(String serviceName);
}
