package com.mobigen.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "open-metadata")
public class NewOpenMetadataConfig {
    private Path path = new Path();
    private String origin;

    @Getter
    @Setter
    public static class Path {
        private String databaseService = "/api/v1/services/databaseServices";
        private String storageService = "/api/v1/services/storageServices";
        private String login = "/api/v1/users/login";
        private String bot = "/api/v1/bots/name/ingestion-bot";
        private String authMechanism = "/api/v1/users/auth-mechanism";
    }
}
