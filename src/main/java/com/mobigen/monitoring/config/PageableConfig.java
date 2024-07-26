package com.mobigen.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pageable-config")
public class PageableConfig {
    private final ServicePageableConfig pageableConfig = new ServicePageableConfig();

    @Getter
    @Setter
    public static class ServicePageableConfig {
        private PageConfig history = new PageConfig();
        private PageConfig connect = new PageConfig();
        private PageConfig event = new PageConfig();
        private PageConfig registration = new PageConfig();
    }

    @Getter
    @Setter
    public static class PageConfig {
        private int page = 0;
        private int size = 5;
    }
}
