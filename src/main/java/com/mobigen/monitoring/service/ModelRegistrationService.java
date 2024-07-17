package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.repository.ModelRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelRegistrationService {

    final OpenMetadataConfig openMetadataConfig;
    final ModelRegistrationRepository modelRegistrationRepository;

    public List<ModelRegistration> getModelRegistrations(int size) {
        return modelRegistrationRepository.findAll(
                PageRequest.of(openMetadataConfig.getPageableConfig().getRegistration().getPage(),
                        size)).getContent();
    }

    public void saveModelRegistrations(List<ModelRegistration> modelRegistrationList) {
        modelRegistrationRepository.saveAll(modelRegistrationList);
    }
}
