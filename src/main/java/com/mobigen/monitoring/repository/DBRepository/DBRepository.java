package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import io.minio.errors.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

public interface DBRepository extends AutoCloseable {
    void getClient(JsonNode serviceJson) throws SQLException, ClassNotFoundException, MinioException;

    int itemsCount() throws SQLException, MinioException, IOException, GeneralSecurityException, Exception;

    void close() throws Exception;
}
