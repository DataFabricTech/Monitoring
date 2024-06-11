package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.dto.ServicesConnect;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesConnectRepository extends JpaRepository<ServicesConnect, Long> {
    // unit of measurement is millisecond
    // UUID, Double
    @Query("SELECT s.name, AVG(sc.connectResponseTime) AS avg_reseponse_time " +
            "FROM Services s JOIN ServicesConnect sc ON s.serviceID = sc.serviceID " +
            "GROUP BY s.name ORDER BY avg_reseponse_time DESC")
    List<Object[]> findTopAverageConnectResponseTimes(Pageable pageable);
    List<ServicesConnect> findTopByOrderByConnectResponseTimeDesc(UUID serviceID, Pageable pageable);
}
