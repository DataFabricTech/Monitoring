package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ConnectionHistoryDTO;
import com.mobigen.monitoring.model.dto.response.ConnectionHistoryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConnectionHistoryRepository extends JpaRepository<ConnectionHistoryDTO, UUID> {

    @Query("select new com.mobigen.monitoring.model.dto.response.ConnectionHistoryResponse(sch.serviceID, s.name, s.displayName, s.serviceType, sch.connectionStatus)" +
            "from ConnectionHistoryDTO as sch left join ServiceDTO as s on sch.serviceID = s.serviceID ")
    List<ConnectionHistoryResponse> findConnectHistoryResponse(Pageable pageable);

    @Query("select new com.mobigen.monitoring.model.dto.response.ConnectionHistoryResponse(sch.serviceID, s.name, s.displayName,s.serviceType, sch.connectionStatus)" +
            "from ConnectionHistoryDTO as sch left join ServiceDTO as s on sch.serviceID = s.serviceID " +
            "where sch.serviceID = ?1")
    List<ConnectionHistoryResponse> findConnectHistoryResponse(UUID serviceID, Pageable pageable);

    List<ConnectionHistoryDTO> findByServiceIDOrderByUpdatedAtDesc(UUID serviceID, Pageable pageable);

    void deleteAllByUpdatedAtLessThan(Long threshold);

    // temp
    List<ConnectionHistoryDTO> findAllByUpdatedAtLessThan(Long cutOffDays);
}
