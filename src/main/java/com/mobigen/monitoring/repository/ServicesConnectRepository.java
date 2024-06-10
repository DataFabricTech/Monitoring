package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.dto.ServicesChange;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicesConnectRepository extends JpaRepository<ServicesChange, Long> {
    // unit of measurement is millisecond
    // UUID, Double
    @Query("SELECT s.serviceID, AVG(s.connectResponseTime) AS avgTime FROM ServicesChange s " +
            "GROUP BY s.serviceID ORDER BY avgTime DESC")
    List<Object[]> findTopAverageConnectResponseTimes(Pageable pageable);
}
