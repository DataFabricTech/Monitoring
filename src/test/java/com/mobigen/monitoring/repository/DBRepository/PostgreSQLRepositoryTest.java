package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.utils.Utils;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.class)
class PostgreSQLRepositoryTest {
    private final Utils utils = new Utils();

    @Container
    private static final PostgreSQLContainer<?> mysql = new PostgreSQLContainer<>("postgres:12.19");
    private static String url;
    private static String user;
    private static String pw;

    private static final List<String> ConnectionFailCode = new ArrayList<>(Arrays.asList("08000", "08001", "08S01", "22000", "90011"));
    private static final List<String> AuthenticationFailCode = new ArrayList<>(Arrays.asList("28000", "08004", "08006", "72000", "28P01"));

    @BeforeAll
    public static void startContainer() {
        mysql.start();
        url = String.format("%s:%d", mysql.getHost(), mysql.getFirstMappedPort());
        user = mysql.getUsername();
        pw = mysql.getPassword();
    }

    @DisplayName("setPostgreSQLRepositoryTest - 기본 값 제공 - 성공")
    @Test
    void setPostgreSQLRepositoryTest() {
        assertDoesNotThrow(() -> new PostgreSQLRepository(setJson(user, pw, url)));
    }

    @DisplayName("setPostgreSQLRepositoryTest - 잘못된 URL 값 제공 - 성공")
    @Test
    void setPostgreSQLRepositoryUrlFailTest() {
        try (var repository = new PostgreSQLRepository(setJson(user, pw, "wrongUrl"))) {
        } catch (SQLException e) {
            assertTrue(ConnectionFailCode.contains(e.getSQLState()));
        } catch (JsonProcessingException e) {
            fail("Json Parsing Error");
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("setPostgreSQLRepositoryTest - 잘못된 Auth 값 제공 - 성공")
    @Test
    void setPostgreSQLRepositoryAuthFailTest() {
        try (var repository = new PostgreSQLRepository(setJson("wrongUser", pw, url))) {
        } catch (SQLException e) {
            assertTrue(AuthenticationFailCode.contains(e.getSQLState()));
        } catch (JsonProcessingException e) {
            fail("Json Parsing Error");
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("itemsCountTest - 성공")
    @Test
    void itemsCountTestTest() {
        try (var repository = new PostgreSQLRepository(setJson(user, pw, url))) {
            assertEquals(194, repository.itemsCount());
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("measureExecuteResponseTimeTest - 성공")
    @Test
    void measureExecuteResponseTimeTest() {
        assertDoesNotThrow(() -> {
            var repository = new PostgreSQLRepository(setJson(user, pw, url));
            repository.measureExecuteResponseTime();
        });
    }

    /**
     * @param args userName, password, url
     * @return json
     */
    JsonNode setJson(String... args) throws JsonProcessingException {
        return utils.getJsonNode(String.format("{\"id\":\"f0051b58-a82c-4664-92d1-24255b969cc4\"," +
                "\"name\":\"fullPostgresConfig\",\"fullyQualifiedName\":\"fullPostgresConfig\"," +
                "\"serviceType\":\"Postgres\",\"description\":\"\",\"connection\":{\"config\":{\"type\":\"Postgres\"," +
                "\"scheme\":\"postgresql+psycopg2\",\"username\":\"%s\",\"authType\":{\"password\":\"%s\"}," +
                "\"hostPort\":\"%s\",\"database\":\"postgres\",\"ingestAllDatabases\":false," +
                "\"sslMode\":\"disable\",\"classificationName\":\"PostgresPolicyTags\"," +
                "\"supportsMetadataExtraction\":true,\"supportsUsageExtraction\":true,\"supportsLineageExtraction\":true," +
                "\"supportsDBTExtraction\":true,\"supportsProfiler\":true,\"supportsDatabase\":true," +
                "\"supportsQueryComment\":true}},\"version\":0.2,\"updatedAt\":1719898190608,\"updatedBy\":\"admin\"," +
                "\"href\":\"secret\",\"changeDescription\":{\"fieldsAdded\":[],\"fieldsUpdated\":[{\"name\":\"connection\"," +
                "\"oldValue\":\"\\\"old-encrypted-value\\\"\",\"newValue\":\"\\\"new-encrypted-value\\\"\"}]," +
                "\"fieldsDeleted\":[],\"previousVersion\":0.1},\"deleted\":false}\n", (Object[]) args));
    }
}