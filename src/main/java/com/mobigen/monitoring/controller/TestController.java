package com.mobigen.monitoring.controller;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequestMapping("/test")
public class TestController {
    private final OpenMetadataConfig openMetadataConfig;

    @Autowired
    public TestController( OpenMetadataConfig openMetadataConfig) {
        this.openMetadataConfig = openMetadataConfig;
    }

    @GetMapping
    public void printConfigValues() {
        System.out.println("??");
        System.out.println(openMetadataConfig.getTable().getGet());
        System.out.println("??");
    }
}
