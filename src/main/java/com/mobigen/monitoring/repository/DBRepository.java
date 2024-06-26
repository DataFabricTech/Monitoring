package com.mobigen.monitoring.repository;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBRepository extends AutoCloseable {
    void getClient(JsonNode connectionConfigJson) throws SQLException;

    long itemsCount();

    void close() throws Exception;
}
