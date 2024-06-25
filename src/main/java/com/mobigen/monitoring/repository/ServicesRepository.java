package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<Services, UUID> {
    long countByConnectionStatusIsTrueAndDeletedIsFalse();
    long countByConnectionStatusIsFalseAndDeletedIsFalse();
    long countServicesByDeletedIsFalse();
    Services findServicesByEntityID(UUID entityID);
}
