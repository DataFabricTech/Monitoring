package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBRepository extends AutoCloseable {
    void getClient(JsonNode connectionConfigJson) throws SQLException, ClassNotFoundException;

    int itemsCount() throws SQLException;

    void close() throws Exception;
}
