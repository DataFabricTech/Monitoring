package com.mobigen.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "entity")
public class OpenMetadataConfig {
    private String origin;
    private final Auth auth = new Auth();
    private final Databases databases = new Databases();
    private final Table table = new Table();
    private final Storage storage = new Storage();
    private final NumberOf numberOf = new NumberOf();

    @Getter
    @Setter
    public static class Auth {
        private String id;
        private String passwd;
    }

    @Getter
    @Setter
    public static class Databases {
        private String get;
        private String list;
    }

    @Getter
    @Setter
    public static class Table {
        private String get;
        private String list;
    }

    @Getter
    @Setter
    public static class Storage {
        private String get;
        private String list;
    }

    @Getter
    @Setter
    public static class NumberOf {
        private int recentChange = 5;
        private int serviceHistory = 5;
    }
}
