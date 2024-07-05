package com.mobigen.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "open-metadata")
public class OpenMetadataConfig {
    private Path path = new Path();
    private String origin;

    private final Auth auth = new Auth();
    private final PageableConfig pageableConfig = new PageableConfig();

    @Getter
    @Setter
    public static class Auth {
        private String id;
        private String passwd;
    }

    @Getter
    @Setter
    public static class PageableConfig {
        private PageConfig history;
        private PageConfig connect;
        private PageConfig event;
        private PageConfig registration;
    }

    @Getter
    @Setter
    public static class PageConfig {
        private int page = 1;
        private int size = 5;
    }

    @Getter
    @Setter
    public static class Path {
        private String databaseService = "/api/v1/services/databaseServices";
        private String storageService = "/api/v1/services/storageServices";
        private String query = "/api/v1/search/query";
        private String login = "/api/v1/users/login";
        private String bot = "/api/v1/bots/name/ingestion-bot";
        private String authMechanism = "/api/v1/users/auth-mechanism";
    }
}
