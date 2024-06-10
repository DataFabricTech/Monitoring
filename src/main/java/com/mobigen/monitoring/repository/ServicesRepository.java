package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.dto.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {
    Long countByConnectionStatusIsTrue();
}
