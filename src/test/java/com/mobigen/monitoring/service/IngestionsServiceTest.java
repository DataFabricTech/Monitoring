package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.IngestionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IngestionsServiceTest {
    @Autowired
    private IngestionsService ingestionsService;

    @AfterEach
    void tearDown() {
        ingestionsService.deleteAll();
    }

    @DisplayName("getIngestionList - default - 성공")
    @Test
    void getIngestionListDefault() {
        assertDoesNotThrow(() -> {
            List<IngestionDTO> ingestionDTOList = new ArrayList<>();
            ingestionDTOList.add(IngestionDTO.builder()
                    .ingestionID(UUID.randomUUID())
                    .name("t")
                    .displayName("t")
                    .type("t")
                    .serviceFQN("")
                    .serviceID(UUID.randomUUID())
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .deleted(false)
                    .build());
            ingestionDTOList.add(IngestionDTO.builder()
                    .ingestionID(UUID.randomUUID())
                    .name("t")
                    .displayName("t")
                    .type("t")
                    .serviceFQN("")
                    .serviceID(UUID.randomUUID())
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            ingestionDTOList.add(IngestionDTO.builder()
                    .ingestionID(UUID.randomUUID())
                    .name("t")
                    .displayName("t")
                    .type("t")
                    .serviceFQN("")
                    .serviceID(UUID.randomUUID())
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            ingestionsService.saveIngestions(ingestionDTOList);

            assertEquals(3, ingestionsService.getIngestionList().size());
        });
    }

    @DisplayName("getIngestionList - null - 성공")
    @Test
    void getIngestionListNullValue() {
        assertEquals(0, ingestionsService.getIngestionList().size());
    }

    @DisplayName("getIngestion - default - 성공")
    @Test
    void getIngestionDefault() {
        assertDoesNotThrow(() -> {
            List<IngestionDTO> ingestionDTOList = new ArrayList<>();
            var uuid = UUID.randomUUID();
            ingestionDTOList.add(IngestionDTO.builder()
                    .ingestionID(uuid)
                    .name("t")
                    .displayName("t")
                    .type("t")
                    .serviceFQN("")
                    .serviceID(UUID.randomUUID())
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .deleted(false)
                    .build());
            ingestionsService.saveIngestions(ingestionDTOList);

            assertEquals(uuid, ingestionsService.getIngestion(uuid).get().getIngestionID());
        });
    }

    @DisplayName("getIngestion - default - 성공")
    @Test
    void getIngestionWrongId() {
        assertTrue(ingestionsService.getIngestion(UUID.randomUUID()).isEmpty());
    }

    @DisplayName("getIngestion - null - 실패")
    @Test
    void getIngestionNull() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> ingestionsService.getIngestion(null));
    }

    @Test
    void saveIngestions() {
        assertDoesNotThrow(() ->{
            List<IngestionDTO> ingestionDTOList = new ArrayList<>();
            ingestionsService.saveIngestions(ingestionDTOList);

            assertEquals(0, ingestionsService.getIngestionList().size());
        });
    }
}