package com.mobigen.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerConfig {
    private String collectExpression = "0 0/30 * * * *";
    private String saveExpression = "0 0/5 * * * *";
    private int connectionTimeout;
}
