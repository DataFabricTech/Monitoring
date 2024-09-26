package com.mobigen.monitoring.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MetadataServiceTest {
    @Autowired
    private MetadataService metadataService;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        metadataService.deleteAll();
    }

    @DisplayName("saveMetadata - default - 성공")
    @Test
    void saveMetadataDefault() {
        assertDoesNotThrow(() -> {
            HashMap<String, String> metadata = new HashMap<>();
            var before = LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            var now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            metadata.put("recent_collected_time", String.valueOf(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            metadata.put("recent_collected_time", String.valueOf(LocalDateTime.now().minusDays(6).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            metadata.put("recent_collected_time", String.valueOf(LocalDateTime.now().minusDays(5).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            metadata.put("recent_collected_time", String.valueOf(LocalDateTime.now().minusDays(4).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            metadata.put("recent_collected_time", String.valueOf(LocalDateTime.now().minusDays(3).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            metadata.put("recent_collected_time", String.valueOf(LocalDateTime.now().minusDays(2).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            metadata.put("recent_collected_time", String.valueOf(before));

            metadataService.saveMetadata(metadata);
            assertEquals(before, metadataService.getRecentCollectedTime());

            metadata.put("recent_collected_time", String.valueOf(now));
            metadataService.saveMetadata(metadata);
            assertEquals(now, metadataService.getRecentCollectedTime());
        });
    }

    @DisplayName("saveMetadata - empty - 성공")
    @Test
    void saveMetadataEmpty() {
        assertDoesNotThrow(() -> {
            HashMap<String, String> metadata = new HashMap<>();
            metadataService.saveMetadata(metadata);
        });
    }
}