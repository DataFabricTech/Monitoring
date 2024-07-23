package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.exception.ErrorCode;
import com.mobigen.monitoring.model.enums.DBType;
import com.mobigen.monitoring.utils.Utils;
import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Unknown;

import java.io.IOException;
import java.net.UnknownHostException;
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
    private final Utils utils = new Utils();

    public MinioRepository(JsonNode serviceJson) {
        getConnection(serviceJson);
    }

    @Override
    public int itemsCount() {
        log.debug("Minio itemCount Start");
        try {
            var buckets = client.listBuckets();

            var fileCount = 0;
            for (var bucket : buckets) {
                for (var ignore : this.client.listObjects(ListObjectsArgs.builder().bucket(bucket.name()).recursive(true).build())) {
                    fileCount++;
                }
            }
            return fileCount;
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.EXECUTE_FAIL)
                    .message(DBType.MINIO.getName())
                    .build();
        }
    }

    @Override
    public void close() throws Exception {
        if (this.client != null)
            client.close();
    }

    @Override
    public Long measureExecuteResponseTime() {
        log.debug("Measure minio execute query response time");
        var start = Instant.now();
        try {
            var bucketArgs = BucketExistsArgs.builder()
                    .bucket("ignore")
                    .build();
            client.bucketExists(bucketArgs);
        } catch (UnknownHostException e) {
            // todo
            throw e;
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.MEASURE_FAIL)
                    .message(DBType.MINIO.getName())
                    .build();
        }
        var end = Instant.now();
        return Duration.between(start, end).toMillis();
    }


    private void getConnection(JsonNode serviceJson) {
        log.debug("Minio getConnection Start");
        var connectionConfigJson = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName()).get(AWS_CONFIG.getName());
        try {
            this.client = MinioClient.builder()
                    .endpoint(utils.getAsTextOrNull(connectionConfigJson.get(END_POINT_URL.getName())))
                    .credentials(utils.getAsTextOrNull(connectionConfigJson.get(AWS_ACCESS_KEY_ID.getName())),
                            utils.getAsTextOrNull(connectionConfigJson.get(AWS_SECRET_ACCESS_KEY.getName())))
                    .build();
        } catch (RuntimeException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.CONNECTION_FAIL)
                    .message(utils.getAsTextOrNull(serviceJson.get(NAME.getName())))
                    .build();
        }
    }
}
