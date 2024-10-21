package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.model.dto.response.ServiceResponse;
import com.mobigen.monitoring.model.enums.ConnectionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<ServiceDTO, UUID> {
    @Query("select new com.mobigen.monitoring.model.dto.response.ServiceResponse(serviceID, name, displayName, " +
            "serviceType, ownerName, createdAt, deleted, connectionStatus) " +
            "from ServiceDTO " +
            "where deleted = ?1")
    List<ServiceResponse> findServiceResponse(boolean deleted, Pageable pageable);
    long countServicesByDeletedIsFalse();
    long countByConnectionStatusAndDeletedIsFalse(ConnectionStatus connectionStatus);

}
