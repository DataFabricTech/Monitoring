package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Iterator;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Repository
@Slf4j
public class MinioRepository implements DBRepository {
    private MinioClient conn;

    @Override
    public void getClient(JsonNode serviceJson) {
        getConnection(serviceJson);
    }

    @Override
    public int itemsCount() throws MinioException, IOException, GeneralSecurityException, Exception {
        log.debug("Minio itemCount Start");
        var buckets = conn.listBuckets();

        var fileCount = 0;
        for (var bucket: buckets) {
            for (var ignore: this.conn.listObjects(ListObjectsArgs.builder().bucket(bucket.name()).recursive(true).build())) {
                fileCount++;
            }
        }
        return fileCount;
    }

    @Override
    public void close() throws Exception {
        if (this.conn != null)
            conn.close();
    }

    private void getConnection(JsonNode serviceJson) {
        log.debug("Minio getConnection Start");
        var connectionConfigJson = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName()).get(AWS_CONFIG.getName());
        try {
            this.conn = MinioClient.builder()
                    .endpoint(connectionConfigJson.get(END_POINT_URL.getName()).asText())
                    .credentials(connectionConfigJson.get(AWS_ACCESS_KEY_ID.getName()).asText(),
                            connectionConfigJson.get(AWS_SECRET_ACCESS_KEY.getName()).asText())
                    .build();
        } catch (Exception e) {
            // todo what kind of exception?
            log.error("Connection fail: " + e + " Service Name :" + serviceJson.get(NAME.getName()).asText());
            throw e;
        }
    }
}
