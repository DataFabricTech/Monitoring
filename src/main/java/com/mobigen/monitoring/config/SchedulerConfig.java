package com.mobigen.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerConfig {
    private String collectExpression = "0 0/30 * * * *";
    private String saveExpression = "0 0/5 * * * *";

    @Bean
    public TaskScheduler collectTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("collectTaskScheduler-");
        return taskScheduler;
    }

    @Bean
    public TaskScheduler saveTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("saveTaskScheduler-");
        return taskScheduler;
    }
}
