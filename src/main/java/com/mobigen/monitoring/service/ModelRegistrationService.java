package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.response.ModelRegistrationResponse;
import com.mobigen.monitoring.repository.ModelRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelRegistrationService {
    private final ModelRegistrationRepository modelRegistrationRepository;

    public void saveModelRegistrations(List<ModelRegistration> modelRegistrationList) {
        modelRegistrationRepository.saveAll(modelRegistrationList);
    }

    public List<ModelRegistrationResponse> getModelRegistrations(PageRequest pageRequest) {
        return modelRegistrationRepository.findModelRegistration(pageRequest);
    }

    public Long getCount() {
        return modelRegistrationRepository.count();
    }
}
