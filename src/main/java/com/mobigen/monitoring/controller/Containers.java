package com.mobigen.monitoring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/containers")
public class Containers {
    /**
     * Get a list of containers
     * Use cursor-based pagination to limit the number
     * entries in the list using `limit` and `before` or `after` query params.
     *
     * @param limit  Limit the number containers returned. (1 to 1000000, default = 10)
     * @param before Returns list of containers before this cursor
     * @param after  Returns list of containers after this cursor
     * @return
     * @throws Exception
     */
    @GetMapping()
    public Object list(@PathVariable(value = "limit", required = false) int limit,
                       @PathVariable(value = "before", required = false) long before,
                       @PathVariable(value = "after", required = false) long after) throws Exception {
        return null;
    }

    /**
     * "Get an Object Store container by `id`.
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public Object get() throws Exception {
        return null;
    }

    /**
     * Get an Container by fully qualified name.
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/name/{fqn}")
    public Object getByName() throws Exception {
        return null;
    }
}
