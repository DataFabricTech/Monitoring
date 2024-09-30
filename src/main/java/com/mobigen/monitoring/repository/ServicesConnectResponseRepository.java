package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ConnectionDTO;
import com.mobigen.monitoring.model.dto.response.ResponseTimeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicesConnectResponseRepository extends JpaRepository<ConnectionDTO, UUID> {
    @Query("select new com.mobigen.monitoring.model.dto.response.ResponseTimeResponse(sc.serviceID, s.name, s.displayName ,sc.executeAt, sc.executeBy, sc.queryExecutionTime) " +
            "from ConnectionDTO as sc left join ServiceDTO as s on sc.serviceID = s.serviceID")
    List<ResponseTimeResponse> findResponseTimeResponse(Pageable pageable);

    @Query("select new com.mobigen.monitoring.model.dto.response.ResponseTimeResponse(sc.serviceID, s.name, s.displayName, sc.executeAt, sc.executeBy, sc.queryExecutionTime) " +
            "from ConnectionDTO as sc left join ServiceDTO as s on sc.serviceID = s.serviceID " +
            "where sc.serviceID = ?1")
    List<ResponseTimeResponse> findResponseTimeResponse(UUID serviceID, Pageable pageable);
}
