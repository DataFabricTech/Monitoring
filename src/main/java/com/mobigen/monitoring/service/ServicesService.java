package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.repository.ServicesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServicesService {
    private final ServicesRepository servicesRepository;

    public long countByConnectionStatusIsConnected() {
        return servicesRepository.countByConnectionStatusAndDeletedIsFalse(CONNECTED);
    }

    public long countByConnectionStatusIsDisconnected() {
        return servicesRepository.countByConnectionStatusAndDeletedIsFalse(DISCONNECTED);
    }

    public long countByConnectionStatusIsConnectError() {
        return servicesRepository.countByConnectionStatusAndDeletedIsFalse(CONNECT_ERROR);
    }

    public Long getCount() {
        return servicesRepository.countServicesByDeletedIsFalse();
    }

    public List<ServiceDTO> getServiceList() {
        return servicesRepository.findAll();
    }

    public Optional<ServiceDTO> getServices(UUID serviceID) {
        return servicesRepository.findById(serviceID);
    }

    public void saveServices(List<ServiceDTO> servicesList) {
        servicesRepository.saveAll(servicesList);
    }
}
