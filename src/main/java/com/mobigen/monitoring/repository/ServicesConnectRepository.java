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
    // unit of measurement is millisecond
    // UUID, Double
//    @Query("SELECT s.name, AVG(sc.endTimestamp-sc.startTimestamp) AS avg_reseponse_time " +
//            "FROM Services s JOIN ServicesConnect sc ON s.serviceID = sc.serviceID " +
//            "GROUP BY s.name ORDER BY avg_reseponse_time DESC")
    @Query(value = "SELECT s.name, AVG(TIMESTAMPDIFF(SECOND, sc.startTimestamp, sc.endTimestamp)) AS avg_response_time " +
            "FROM Services s " +
            "JOIN ServicesConnect sc ON s.serviceID = sc.serviceID " +
            "GROUP BY s.name " +
            "ORDER BY avg_response_time DESC", nativeQuery = true)
    List<Object[]> findTopAverageConnectResponseTimes(Pageable pageable);
    List<ServicesConnect> findTopByOrderByEndTimestampDesc(UUID serviceID, Pageable pageable);
    ServicesConnect findServicesConnectByServiceName(String serviceName);
}
