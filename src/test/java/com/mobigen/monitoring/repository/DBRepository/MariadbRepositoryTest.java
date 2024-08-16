package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.utils.Utils;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MariadbRepositoryTest {
    private final Utils utils = new Utils();

    @Container
    private static final MariaDBContainer mariaDB = new MariaDBContainer("mariadb:11.4.2");
    private static String url;
    private static String user;
    private static String pw;

    private static final List<String> ConnectionFailCode = new ArrayList<>(Arrays.asList("08000", "08001", "08S01", "22000", "90011"));
    private static final List<String> AuthenticationFailCode = new ArrayList<>(Arrays.asList("28000", "08004", "08006", "72000", "28P01"));

    @BeforeAll
    public static void startContainer() {
        mariaDB.start();
        url = String.format("%s:%d", mariaDB.getHost(), mariaDB.getFirstMappedPort());
        user = mariaDB.getUsername();
        pw = mariaDB.getPassword();
    }


    @DisplayName("setMariadbRepository - 정상 값 제공 - 성공")
    @Test
    void setMariadbRepositoryTest() {
        assertDoesNotThrow(() -> {
            var serviceJson = setJson(user, pw, url);
            new MariadbRepository(serviceJson);
        });
    }

    @DisplayName("setMariadbRepository - 잘못된 Url 값 제공 - 성공")
    @Test
    void setMariadbRepositoryUrlFailTest() {
        try (var repository = new MariadbRepository(setJson(user, pw, "wrongUrl"))) {
        } catch (SQLException e) {
            assertTrue(ConnectionFailCode.contains(e.getSQLState()));
        } catch (JsonProcessingException e) {
            fail("Json Parsing Error");
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("setMariadbRepository - 잘못된 Auth 값 제공 - 성공")
    @Test
    void setMariadbRepositoryAuthFailTest() {
        try (var repository = new MariadbRepository(setJson("wrongUser", pw, url))) {
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
    void itemsCountTest() {
        try (var repository = new MariadbRepository(setJson(user, pw, url))) {
            assertEquals(82, repository.itemsCount());
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("measureExecuteResponseTimeTest - 성공")
    @Test
    void measureExecuteResponseTimeTest() {
        assertDoesNotThrow(() -> {
            var repository = new MariadbRepository(setJson(user,pw, url));
            repository.measureExecuteResponseTime();
        });
    }

    /**
     * @param args userName, password, url
     * @return json
     */
    JsonNode setJson(String... args) throws JsonProcessingException {
        return utils.getJsonNode(String.format("{\"id\":\"74eddc8b-69c6-4c4c-a77b-62cba7e27a1f\"," +
                "\"name\":\"fullMariadbConfig\", \"fullyQualifiedName\":\"fullMariadbConfig\"," +
                "\"serviceType\":\"MariaDB\",\"description\":\"\", \"connection\":{\"config\":" +
                "{\"type\":\"MariaDB\",\"scheme\":\"mysql+pymysql\",\"username\":\"%s\"," +
                "\"password\":\"%s\",\"hostPort\":\"%s\"," +
                "\"supportsMetadataExtraction\":true,\"supportsDBTExtraction\":true,\"supportsProfiler\":true," +
                "\"supportsQueryComment\":true}},\"version\":0.1,\"updatedAt\":1719452002810,\"updatedBy\":\"admin\"," +
                "\"href\":\"secret\"," +
                "\"deleted\":false}", (Object[]) args));
    }
}