package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.utils.Utils;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

// 현재 맥북 환경에서 build할 것이 아닌, 개발환경에서 test할 것이기 때문에,
@Testcontainers
@TestMethodOrder(MethodOrderer.class)
class OracleRepositoryTest {
    private final Utils utils = new Utils();
    private static final String arch = System.getProperty("os.arch");
    private static String url;
    private static String user;
    private static String pw;
    private static OracleContainer oracle;

    private static final List<String> ConnectionFailCode = new ArrayList<>(Arrays.asList("08000", "08001", "08S01", "22000", "90011"));
    private static final List<String> AuthenticationFailCode = new ArrayList<>(Arrays.asList("28000", "08004", "08006"));

    @BeforeAll
    public static void startContainer() {
        assumeTrue(arch.equals("amd64") || arch.equals("x86_64"), "Skipping test for amd64");
        if (oracle == null)
            oracle = new OracleContainer("gvenzl/oracle-free:23.4-slim-faststart")
                    .withDatabaseName("testDB")
                    .withUsername("testUser")
                    .withPassword("testPassword");
        oracle.start();
        url = String.format("%s:%d", oracle.getHost(), oracle.getFirstMappedPort());
        user = oracle.getUsername();
        pw = oracle.getPassword();
    }

    @DisplayName("setMysqlRepositoryTest - 기본 값 제공 - 성공")
    @Test
    void setMysqlRepositoryTest() {
        assertDoesNotThrow(() -> new OracleRepository(setJson(user, pw, url)));
    }

    @DisplayName("setMysqlRepositoryTest - 기본 값 제공 - 성공")
    @Test
    void setMysqlRepositoryUrlFailTest() {
        try (var repository = new MysqlRepository(setJson(user, pw, "wrongUrl"))) {
        } catch (SQLException e) {
            assertTrue(ConnectionFailCode.contains(e.getSQLState()));
        } catch (JsonProcessingException e) {
            fail("Json Parsing Error");
        } catch (Exception e) {
            fail("Close Error");
        }
    }

    @DisplayName("setMysqlRepositoryTest - 기본 값 제공 - 성공")
    @Test
    void setMysqlRepositoryAuthFailTest() {
        try (var repository = new MysqlRepository(setJson("wrongUser", pw, url))) {
        } catch (SQLException e) {
            assertTrue(AuthenticationFailCode.contains(e.getSQLState()));
        } catch (JsonProcessingException e) {
            fail("Json Parsing Error");
        } catch (Exception e) {
            fail("Close Error");
        }
    }

    @DisplayName("itemsCount - 성공")
    @Test
    void itemsCount() {
        try (var repository = new MysqlRepository(setJson(user, pw, url))) {
            assertEquals(88, repository.itemsCount());
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("measureExecuteResponseTime - 성공")
    @Test
    void measureExecuteResponseTime() {
        assertDoesNotThrow(() -> {
            var repository = new MysqlRepository(setJson(user,pw, url));
            repository.measureExecuteResponseTime();
        });
    }

    /**
     * @param args userName, password, url, TNSConnection
     * @return json
     */
    JsonNode setJson(String... args) throws JsonProcessingException {
        return utils.getJsonNode(String.format("{\"id\":\"58f147b6-0dce-4a32-aed4-07aa4442ed6b\"," +
                "\"name\":\"fullOracleConfig\",\"fullyQualifiedName\":\"fullOracleConfig\",\"serviceType\":\"Oracle\"," +
                "\"description\":\"\",\"connection\":{\"config\":{\"type\":\"Oracle\",\"scheme\":\"oracle+cx_oracle\"," +
                "\"username\":\"%s\",\"password\":\"%s\",\"hostPort\":\"%s\"," +
                "\"oracleConnectionType\":{\"oracleTNSConnection\":\"%s\"}," +
                "\"instantClientDirectory\":\"/instantclient\",\"supportsMetadataExtraction\":true," +
                "\"supportsUsageExtraction\":true,\"supportsLineageExtraction\":true,\"supportsDBTExtraction\":true," +
                "\"supportsProfiler\":true,\"supportsQueryComment\":true}},\"version\":0.3," +
                "\"updatedAt\":1719821779506,\"updatedBy\":\"admin\"," +
                "\"href\":\"secret\"," +
                "\"changeDescription\":{\"fieldsAdded\":[]," +
                "\"fieldsUpdated\":[{\"name\":\"connection\",\"oldValue\":\"\\\"old-encrypted-value\\\"\"," +
                "\"newValue\":\"\\\"new-encrypted-value\\\"\"}],\"fieldsDeleted\":[],\"previousVersion\":0.2}," +
                "\"deleted\":false}", (Object[]) args));
    }
}