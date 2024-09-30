package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.IngestionHistoryDTO;
import com.mobigen.monitoring.model.dto.response.IngestionHistoryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IngestionHistoryRepository extends JpaRepository<IngestionHistoryDTO, UUID> {
    @Query("select new com.mobigen.monitoring.model.dto.response.IngestionHistoryResponse(ih.eventAt, i.displayName, i.type, ih.event, ih.state, i.serviceID, s.name, s.displayName, s.serviceType)" +
            "from IngestionHistoryDTO as ih left join IngestionDTO as i on ih.ingestionID = i.ingestionID left join ServiceDTO as s on i.serviceID = s.serviceID")
    List<IngestionHistoryResponse> findIngestionHistoryResponse(Pageable pageable);


    Optional<IngestionHistoryDTO> findIngestionHistoryDTOByIngestionRunId(UUID ingestionRunID);

    void deleteAllByEventAtLessThan(Long cutOffEventAt);
}
