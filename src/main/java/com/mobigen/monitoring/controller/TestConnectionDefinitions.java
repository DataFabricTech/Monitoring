package com.mobigen.monitoring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequestMapping("/TestConnectionDefinitions")
public class TestConnectionDefinitions {
    /**
     * Get a list of test connection definitions. Use cursor-based pagination to limit the number
     * entries in the list using `limit` and `before` or `after` query params.
     *
     * @param limit  Limit the number containers returned. (1 to 1000000, default = 10)
     * @param before Returns list of containers before this cursor
     * @param after  Returns list of containers after this cursor
     * @return
     * @throws Exception
     */
    @GetMapping
    public Object list(
            @PathVariable(value = "limit", required = false) int limit,
            @PathVariable(value = "before", required = false) String before,
            @PathVariable(value = "after", required = false) String after) throws Exception {
        return null;
    }

    /**
     * Get a Test Connection Definition by `Id`.
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public Object get() throws Exception {
        return null;
    }

    /**
     * Get a test connection definition by `name`.
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/name/{name}")
    public Object getByName() throws Exception {
        return null;
    }
}
