package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesChildRepository extends JpaRepository<ServicesChild, UUID> {
    ServicesChild findServicesChildByEntityID(UUID entityID);
    List<ServicesChild> findServicesChildrenByServiceID(UUID serviceID);
}
