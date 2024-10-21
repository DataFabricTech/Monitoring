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
    @Query("select new com.mobigen.monitoring.model.dto.response.ModelRegistrationResponse(s.serviceID, s.name, s.displayName, m.omModelCount, m.modelCount) " +
    "from ServiceDTO as s left join ModelRegistration as m on m.serviceId = s.serviceID where s.deleted = ?1")
    List<ModelRegistrationResponse> findModelRegistration(boolean deleted, Pageable pageable);
}
