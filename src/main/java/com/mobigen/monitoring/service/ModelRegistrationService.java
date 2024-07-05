package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.repository.ModelRegistrationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelRegistrationService {

    final OpenMetadataConfig openMetadataConfig;
    final ModelRegistrationRepository modelRegistrationRepository;

    public ModelRegistrationService(OpenMetadataConfig openMetadataConfig, ModelRegistrationRepository modelRegistrationRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.modelRegistrationRepository = modelRegistrationRepository;
    }

    public List<ModelRegistration> getModelRegistrations(int size) {
        return modelRegistrationRepository.findAll(
                PageRequest.of(openMetadataConfig.getPageableConfig().getRegistration().getPage()-1,
                        size)).getContent();
    }
}
