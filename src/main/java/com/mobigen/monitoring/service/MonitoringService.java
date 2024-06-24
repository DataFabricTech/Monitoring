package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.NewOpenMetadataConfig;
import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.config.SchedulerConfig;
import com.mobigen.monitoring.controller.Monitoring;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MonitoringService {
    OpenMetadataConfig openMetadataConfig;
    SchedulerConfig schedulerConfig;
    OpenMetadataService openMetadataService;

    public MonitoringService(OpenMetadataConfig openMetadataConfig, SchedulerConfig schedulerConfig, OpenMetadataService openMetadataService) {
        this.openMetadataConfig = openMetadataConfig;
        this.schedulerConfig = schedulerConfig;
        this.openMetadataService = openMetadataService;
    }


    @Scheduled(cron = "${scheduler.expression:0 5 * * *}")
    public void scheduler() {
        log.debug("Monitoring Start");

        List<Object> databaseServices = openMetadataService.getDatabaseServices();
        List<Object> storageServices = openMetadataService.getStorageServices();

        for (var databaseService: databaseServices) {
            System.out.println(databaseService);
        }
    }
}
