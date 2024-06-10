package com.mobigen.monitoring.controller.services;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storageService")
public class StorageService {
    /**
     * Get a list of storage services.
     *
     * @param limit Limit the number containers returned. (1 to 1000000, default = 10)
     * @param before Returns list of containers before this cursor
     * @param after Returns list of containers after this cursor
     * @return
     * @throws Exception
     */
    @GetMapping
    public Object list(@PathVariable(value = "limit",required = false) int limit,
                       @PathVariable(value = "before", required = false) String before,
                       @PathVariable(value = "after", required = false) String after) throws Exception {
        return null;
    }

    /**
     * Get an storage service by `id`.
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public Object get() throws Exception {
        return null;
    }

    /**
     * Get a storage service by the service `name`.
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/name/{name}")
    public Object getByName() throws Exception {
        return null;
    }
}
