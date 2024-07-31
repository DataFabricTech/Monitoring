package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.utils.Utils;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.class)
class MysqlRepositoryTest {
    private final Utils utils = new Utils();

    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.0.1");
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

    @DisplayName("setMysqlRepositoryTest - 기본 값 제공 - 성공")
    @Test
    void setMysqlRepositoryTest() {
        assertDoesNotThrow(() -> new MysqlRepository(setJson(user, pw, url)));
    }

    @DisplayName("setMysqlRepositoryTest - 잘못된 URL 값 제공 - 성공")
    @Test
    void setMysqlRepositoryUrlFailTest() {
        try (var repository = new MysqlRepository(setJson(user, pw, "wrongUrl"))) {
        } catch (SQLException e) {
            assertTrue(ConnectionFailCode.contains(e.getSQLState()));
        } catch (JsonProcessingException e) {
            fail("Json Parsing Error");
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("setMysqlRepositoryTest - 잘못된 Auth 값 제공 - 성공")
    @Test
    void setMysqlRepositoryAuthFailTest() {
        try (var repository = new MysqlRepository(setJson("wrongUser", pw, url))) {
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
        try (var repository = new MysqlRepository(setJson(user, pw, url))) {
            assertEquals(88, repository.itemsCount());
        } catch (Exception e) {
            fail(e);
        }
    }

    @DisplayName("measureExecuteResponseTimeTest - 성공")
    @Test
    void measureExecuteResponseTimeTest() {
        assertDoesNotThrow(() -> {
            var repository = new MysqlRepository(setJson(user,pw, url));
            repository.measureExecuteResponseTime();
        });
    }

    /**
     * @param args userName, password, url
     * @return json
     */
    JsonNode setJson(String... args) throws JsonProcessingException {
        return utils.getJsonNode(String.format("{\"id\":\"415a9c2d-2ec2-4a93-b92f-063057dca6e1\"," +
                "\"name\":\"fullMysqlConfig\",\"fullyQualifiedName\":\"fullMysqlConfig\",\"serviceType\":\"Mysql\"," +
                "\"description\":\"\",\"connection\":{\"config\":{\"type\":\"Mysql\",\"scheme\":\"mysql+pymysql\"," +
                "\"username\":\"%s\",\"authType\":{\"password\":\"%s\"},\"hostPort\":\"%s\"," +
                "\"supportsMetadataExtraction\":true,\"supportsDBTExtraction\":true,\"supportsProfiler\":true," +
                "\"supportsQueryComment\":true}},\"version\":0.1,\"updatedAt\":1720059708067,\"updatedBy\":\"admin\"," +
                "\"href\":\"secret\"," +
                "\"deleted\":false}", (Object[]) args));
    }
}