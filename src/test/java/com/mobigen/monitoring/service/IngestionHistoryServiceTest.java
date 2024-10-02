package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.IngestionDTO;
import com.mobigen.monitoring.model.dto.IngestionHistoryDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IngestionHistoryServiceTest {
    @Autowired
    private IngestionHistoryService ingestionHistoryService;
    @Autowired
    private IngestionsService ingestionsService;

    private final UUID ingestionId = UUID.randomUUID();


    @BeforeEach
    void setUp() {
        List<IngestionDTO> ingestionDTOList = new ArrayList<>();
        ingestionDTOList.add(IngestionDTO.builder()
                .ingestionID(ingestionId)
                .name("t")
                .displayName("t")
                .type("t")
                .build());

        ingestionsService.saveIngestions(ingestionDTOList);
    }

    @AfterEach
    void tearDown() {
        ingestionHistoryService.deleteAll();
        ingestionsService.deleteAll();
    }

    @DisplayName("getIngestionHistoryResponse - default - 성공")
    @Test
    void getIngestionHistoryResponseDefault() {
        assertDoesNotThrow(() -> {
            List<IngestionHistoryDTO> ingestionHistoryDTOList = new ArrayList<>();
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(UUID.randomUUID())
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryService.saveIngestionHistories(ingestionHistoryDTOList);

            assertEquals(1, ingestionHistoryService.getIngestionHistoryResponse(PageRequest.of(0, 10)).size());
        });
    }

    @DisplayName("getIngestionHistoryResponse - null - 성공")
    @Test
    void getIngestionHistoryResponseNull() {
        assertDoesNotThrow(() -> {
            assertEquals(0, ingestionHistoryService.getIngestionHistories(PageRequest.of(0, 10)).size());
        });
    }

    @DisplayName("getIngestionHistories - default - 성공")
    @Test
    void getIngestionHistoriesDefault() {
        assertDoesNotThrow(() -> {
            List<IngestionHistoryDTO> ingestionHistoryDTOList = new ArrayList<>();
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(UUID.randomUUID())
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryService.saveIngestionHistories(ingestionHistoryDTOList);

            assertEquals(1, ingestionHistoryService.getIngestionHistoryResponse(PageRequest.of(0, 10)).size());
        });
    }

    @DisplayName("getIngestionHistories - null - 성공")
    @Test
    void getIngestionHistories() {
        assertEquals(0, ingestionHistoryService.getIngestionHistoryResponse(PageRequest.of(0, 10)).size());
    }

    @DisplayName("getCount - empty - 성공")
    @Test
    void getCountEmpty() {
        assertEquals(0, ingestionHistoryService.getCount());
    }

    @DisplayName("getCount - exist - 성공")
    @Test
    void getCount() {
        assertDoesNotThrow(() -> {
            List<IngestionHistoryDTO> ingestionHistoryDTOList = new ArrayList<>();
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(UUID.randomUUID())
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryService.saveIngestionHistories(ingestionHistoryDTOList);

            assertEquals(1, ingestionHistoryService.getCount());
        });
    }

    @DisplayName("getIngestionHistory - default - 성공")
    @Test
    void getIngestionHistoryDefault() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            List<IngestionHistoryDTO> ingestionHistoryDTOList = new ArrayList<>();
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryService.saveIngestionHistories(ingestionHistoryDTOList);

            assertEquals(uuid, ingestionHistoryService.getIngestionHistory(uuid).get().getIngestionRunId());
        });

    }

    @DisplayName("getIngestionHistory - wrong id - 성공")
    @Test
    void getIngestionHistoryWrongId() {
        assertTrue(ingestionHistoryService.getIngestionHistory(UUID.randomUUID()).isEmpty());
    }

    @DisplayName("saveIngestionHistories - empty - pass")
    @Test
    void saveIngestionHistories() {
        assertDoesNotThrow(() -> {
            ingestionHistoryService.saveIngestionHistories(new ArrayList<>());
            assertEquals(0, ingestionHistoryService.getIngestionHistories(PageRequest.of(0, 10)).size());
        });
    }

    @DisplayName("deleteIngestionHistories - default - 성공")
    @Test
    void deleteIngestionHistoriesDefault() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            var uuid2 = UUID.randomUUID();
            var uuid3 = UUID.randomUUID();
            List<IngestionHistoryDTO> ingestionHistoryDTOList = new ArrayList<>();
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid2)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().minusDays(3).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid3)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryService.saveIngestionHistories(ingestionHistoryDTOList);
            ingestionHistoryService.deleteIngestionHistories(2);
            assertEquals(2, ingestionHistoryService.getIngestionHistories(PageRequest.of(0, 10, Sort.by("eventAt").descending())).size());
            assertEquals(uuid, ingestionHistoryService.getIngestionHistories(PageRequest.of(0,10, Sort.by("eventAt").descending())).getFirst().getIngestionRunId());
            assertEquals(uuid2, ingestionHistoryService.getIngestionHistories(PageRequest.of(0,10, Sort.by("eventAt").descending())).getLast().getIngestionRunId());
        });
    }

    @DisplayName("deleteIngestionHistories - two type cut off - 성공")
    @Test
    void deleteIngestionHistoriesTwoN() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            var uuid2 = UUID.randomUUID();
            var uuid3 = UUID.randomUUID();
            List<IngestionHistoryDTO> ingestionHistoryDTOList = new ArrayList<>();
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid2)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().minusDays(3).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid3)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryService.saveIngestionHistories(ingestionHistoryDTOList);
            ingestionHistoryService.deleteIngestionHistories(2);
            assertEquals(2, ingestionHistoryService.getIngestionHistories(PageRequest.of(0, 10, Sort.by("eventAt").descending())).size());
            assertEquals(uuid, ingestionHistoryService.getIngestionHistories(PageRequest.of(0,10, Sort.by("eventAt").descending())).getFirst().getIngestionRunId());
            assertEquals(uuid2, ingestionHistoryService.getIngestionHistories(PageRequest.of(0,10, Sort.by("eventAt").descending())).getLast().getIngestionRunId());
            ingestionHistoryService.deleteIngestionHistories(1);
            assertEquals(1, ingestionHistoryService.getIngestionHistories(PageRequest.of(0, 10, Sort.by("eventAt").descending())).size());
            assertEquals(uuid, ingestionHistoryService.getIngestionHistories(PageRequest.of(0,10, Sort.by("eventAt").descending())).getFirst().getIngestionRunId());
        });
    }

    @DisplayName("deleteIngestionHistories - minus value insert - 성공")
    @Test
    void deleteIngestionHistories() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            var uuid2 = UUID.randomUUID();
            var uuid3 = UUID.randomUUID();
            List<IngestionHistoryDTO> ingestionHistoryDTOList = new ArrayList<>();
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid2)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryDTOList.add(IngestionHistoryDTO.builder()
                    .eventAt(LocalDateTime.now().minusDays(3).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .ingestionID(ingestionId)
                    .ingestionRunId(uuid3)
                    .event("Add")
                    .state("-")
                    .build());
            ingestionHistoryService.saveIngestionHistories(ingestionHistoryDTOList);
            ingestionHistoryService.deleteIngestionHistories(-5);
            assertEquals(3, ingestionHistoryService.getIngestionHistories(PageRequest.of(0, 10)).size());
        });
    }
}