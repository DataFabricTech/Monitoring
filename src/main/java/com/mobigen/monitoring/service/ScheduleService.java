package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.repository.ServicesRepository;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;

public class ScheduleService {
    OpenMetadataConfig openMetadataConfig;
    ServicesRepository servicesRepository;

    public ScheduleService(OpenMetadataConfig openMetadataConfig, ServicesRepository servicesRepository
                           ) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesRepository = servicesRepository;
    }

    /**
     * 1.
     * @param services
     */

    public void insertServices(Services services) {
        try {
//            var change = !servicesRepository.existsById(services.getServiceID()) ?
//                    ServicesChange.builder()
//                            .serviceID(services.getServiceID())
//                            .createdAt(LocalDateTime.now())
//                            .description("TODO Enum혹은 yml 파일을 이용한 event 범위 확장에 대해 생각해볼것")
//                            .build() :
//                    ServicesChange.builder()
//                            .serviceID(services.getServiceID())
//                            .updatedAt(LocalDateTime.now())
//                            .build();
//
//            var event = !servicesRepository.existsById(services.getServiceID()) ?
//                    ServicesEvent.builder()
//                            .serviceID(services.getServiceID())
//                            .event("TODO Enum혹은 yml 파일을 이용한 event 범위 확장에 대해 생각해볼것1")
//                            .description("TODO Enum혹은 yml 파일을 이용한 event 범위 확장에 대해 생각해볼것")
//                            .eventOccurredAt(LocalDateTime.now())
//                            .build() :
//                    ServicesEvent.builder()
//                            .serviceID(services.getServiceID())
//                            .event("TODO Enum혹은 yml 파일을 이용한 event 범위 확장에 대해 생각해볼것")
//                            .description("TODO Enum혹은 yml 파일을 이용한 event 범위 확장에 대해 생각해볼것")
//                            .eventOccurredAt(LocalDateTime.now())
//                            .build();
//
//            servicesChangeRepository.save(change);
//            servicesEventRepository.save(event);

            servicesRepository.save(services);
        } catch (IllegalStateException | NullPointerException | DataAccessException e) {
            // todo
        }
    }
}
