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
    @Query(value = "SELECT s.entity_id, AVG(EXTRACT(EPOCH FROM (sc.end_timestamp - sc.start_timestamp))) AS avg_response_time_seconds " +
            "FROM services s JOIN services_connect sc ON s.entity_id = sc.service_id " +
            "GROUP BY s.entity_id " +
            "ORDER BY avg_response_time_seconds DESC;", nativeQuery = true)
    List<Object[]> findServiceIdAndAverageConnectionResponseTime(Pageable pageable);
    List<ServicesConnect> findByServiceIDOrderByEndTimestampDesc(UUID serviceID, Pageable pageable);

}
