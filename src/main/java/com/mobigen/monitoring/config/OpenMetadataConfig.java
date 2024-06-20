package com.mobigen.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private final PageableConfig pageableConfig = new PageableConfig();
    private final SaveEntityType saveEntityType = new SaveEntityType();

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
    public static class PageableConfig {
        private PageConfig history;
        private PageConfig connect;
        private PageConfig event;
    }


    @Getter
    @Setter
    public static class PageConfig {
        private int page = 1;
        private int size = 5;
    }

    @Getter
    @Setter
    public static class SaveEntityType {
        private List<String> history;
        private List<String> services;
        private List<String> connect;
        private List<String> servicesChild;
    }
}
