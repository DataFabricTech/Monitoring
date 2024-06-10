package com.mobigen.monitoring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tables")
public class TableController {
    /**
     * Get a list of tables, optionally filtered by `database` it belongs to.
     * Use cursor-based pagination to limit the number
     * entries in the list using `limit` and `before` or `after` query params.
     *
     * @param limit  Limit the number containers returned. (1 to 1000000, default = 10)
     * @param before Returns list of containers before this cursor
     * @param after  Returns list of containers after this cursor
     * @return
     * @throws Exception
     */
    @GetMapping
    public Object list(@PathVariable(value = "database", required = false) String database,
                       @PathVariable(value = "limit", required = false) int limit,
                       @PathVariable(value = "before", required = false) String before,
                       @PathVariable(value = "after", required = false) String after) throws Exception {
        return null;
    }

    /**
     * "Get a table by `Id`
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public Object get() throws Exception {
        return null;
    }

    /**
     * Get a table by fully qualified table name.
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/name/{fqn}")
    public Object getByName() throws Exception {
        return null;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/lastUpdated/{id}")
    public Object getUpdated() throws Exception {
        return null;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/lastUpdated/name/{fqn}")
    public Object getUpdatedByName() throws Exception {
        return null;
    }
}
