package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.utils.Utils;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.class)
class MinioRepositoryTest {
    private final Utils utils = new Utils();

    @Container
    private static final MinIOContainer minioContainer = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z");
    private static String url;
    private static String user;
    private static String pw;

    @BeforeAll
    public static void startContainer() {
        minioContainer.start();
        url = String.format("%s:%d", minioContainer.getHost(), minioContainer.getFirstMappedPort());
        user = minioContainer.getUserName();
        pw = minioContainer.getPassword();
    }

    /**
     * MinioRepository
     * 1. URL 실패
     * 2. Auth 실패
     * 3. 성공
     */
    @DisplayName("setMinioRepositoryTest - 기본 값 제공 - 정상")
    @Test
    void setMinioRepositoryTest() {
        assertDoesNotThrow(() -> new MinioRepository(setJson(user, pw, url)));
    }

    @DisplayName("setMinioRepositoryTest - 잘못된 URL 값 제공 - 정상")
    @Test
    void setMinioRepositoryUrlFailTest() {
        try (var repository = new MinioRepository(setJson(user, pw, "wrongUrl"))) {
        } catch (UnknownHostException e) {
            // Test Success
        } catch (JsonProcessingException e) {
            fail("Json parsing Error");
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("setMinioRepositoryTest - 잘못된 Auth 값 제공 - 정상")
    @Test
    void setMinioRepositoryAuthFailTest() {
        try (var repository = new MinioRepository(setJson("WrongId", pw, url))) {
        } catch (MinioException e) {
            // Test Success
        } catch (JsonProcessingException e) {
            fail("Json parsing Error");
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("itemCountTest - 성공")
    @Test
    void itemsCountTest() {
        try (var repository = new MinioRepository(setJson(user, pw, url))) {
            assertEquals(0, repository.itemsCount());
        } catch (JsonProcessingException e) {
            fail("Json parsing error");
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("measureExecuteResponseTimeTest - 성공")
    @Test
    void measureExecuteResponseTimeTest() {
        assertDoesNotThrow(() -> {
            var repository = new MinioRepository(setJson(user,pw,url));
            repository.measureExecuteResponseTime();
        });
    }

    /**
     * @param args userName, password, region, url
     * @return json
     */
    JsonNode setJson(String... args) throws JsonProcessingException {
        return utils.getJsonNode(String.format("{\"id\":\"67abe23f-a420-4cd3-9081-579ed1fc147d\"," +
                "\"name\":\"fullminioConfig\",\"fullyQualifiedName\":\"fullminioConfig\",\"serviceType\":\"S3\"," +
                "\"description\":\"\",\"connection\":{\"config\":{\"type\":\"S3\",\"awsConfig\":" +
                "{\"awsAccessKeyId\":\"%s\",\"awsSecretAccessKey\":\"%s\",\"awsRegion\":" +
                "\"ap-northeast-2\",\"endPointURL\":\"http://%s\",\"assumeRoleSessionName\":" +
                "\"OpenMetadataSession\"},\"bucketNames\":[],\"supportsMetadataExtraction\":true}},\"version\":0.2," +
                "\"updatedAt\":1719989986575,\"updatedBy\":\"admin\"," +
                "\"href\":\"secret\",\"changeDescription\":{\"fieldsAdded\":[],\"fieldsUpdated\":[{\"name\":" +
                "\"connection\",\"oldValue\":\"\\\"old-encrypted-value\\\"\",\"newValue\":" +
                "\"\\\"new-encrypted-value\\\"\"}],\"fieldsDeleted\":[],\"previousVersion\":0.1}," +
                "\"deleted\":false}", (Object[]) args));
    }
}