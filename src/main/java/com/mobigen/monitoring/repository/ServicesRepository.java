package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.model.enums.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<ServiceDTO, UUID> {
    long countServicesByDeletedIsFalse();
    long countByConnectionStatusAndDeletedIsFalse(ConnectionStatus connectionStatus);
}
