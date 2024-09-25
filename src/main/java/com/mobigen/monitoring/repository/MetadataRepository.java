package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.MetadataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MetadataRepository extends JpaRepository<MetadataDTO, UUID> {
    @Query("select value from MetadataDTO where name = 'recent_collected_time'")
    String getRecentCollectedTime();
}
