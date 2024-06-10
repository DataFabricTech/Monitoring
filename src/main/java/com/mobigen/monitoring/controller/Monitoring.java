package com.mobigen.monitoring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Monitoring {
    /**
     * for manual monitoring data collect start
     * @return Response for status
     */
    @GetMapping
    public Object list() throws Exception {
        return null;
    }
}
