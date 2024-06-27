package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.SQLException;

public class PostgreSQLRepository implements DBRepository{
    @Override
    public void getClient(JsonNode connectionConfigJson) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

    @Override
    public int itemsCount() throws SQLException {
        return 0;
    }

    @Override
    public void close() throws Exception {

    }
}
