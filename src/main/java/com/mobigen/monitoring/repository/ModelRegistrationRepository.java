package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.response.ModelRegistrationResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModelRegistrationRepository extends JpaRepository<ModelRegistration, UUID> {
    @Query("select new com.mobigen.monitoring.model.dto.response.ModelRegistrationResponse(m.serviceId, s.name, s.displayName, m.updatedAt, m.omModelCount, m.modelCount) " +
            "from ModelRegistration as m left join ServiceDTO as s on m.serviceId = s.serviceID")
    List<ModelRegistrationResponse> findModelRegistration(Pageable pageable);
}
