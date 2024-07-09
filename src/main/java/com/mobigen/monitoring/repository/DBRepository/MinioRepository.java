package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Slf4j
public class MinioRepository implements DBRepository {
    private MinioClient client;

    public MinioRepository(JsonNode serviceJson) {
        getConnection(serviceJson);
    }

    @Override
    public int itemsCount() throws ErrorResponseException, InsufficientDataException, InternalException,
            InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException,
            ServerException, XmlParserException {
        log.debug("Minio itemCount Start");
        var buckets = client.listBuckets();

        var fileCount = 0;
        for (var bucket : buckets) {
            for (var ignore : this.client.listObjects(ListObjectsArgs.builder().bucket(bucket.name()).recursive(true).build())) {
                fileCount++;
            }
        }
        return fileCount;
    }

    @Override
    public void close() throws Exception {
        if (this.client != null)
            client.close();
    }

    @Override
    public Long measureExecuteResponseTime() throws MinioException, InvalidKeyException, IOException, NoSuchAlgorithmException {
        log.debug("Measure minio execute query response time");
        var start = Instant.now();
        try {
            var bucketArgs = BucketExistsArgs.builder()
                    .bucket("test")
                    .build();
            client.bucketExists(bucketArgs);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.debug("Measure minio execute response time error");
            throw e;
        }
        var end = Instant.now();
        return Duration.between(start, end).toMillis();
    }


    private void getConnection(JsonNode serviceJson) {
        log.debug("Minio getConnection Start");
        var connectionConfigJson = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName()).get(AWS_CONFIG.getName());
        try {
            this.client = MinioClient.builder()
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
