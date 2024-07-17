package com.mobigen.monitoring.controller;

import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.recordModel;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(
        name = "Monitoring",
        description =
                "데이터 페브릭의 모니터링을 위한 API로써, 서비스들의 저장소 유형 요약, 서비스들의 연결 상태 요약, 서비스들의 평균 응답 시간, " +
                        "데이터 모델 추천 순위, 데이터 모델 등록 현황, 히스토리를 제공하는 도구입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/monitoring")
public class Monitoring {

    final ServicesService servicesService;
    final ConnectService connectService;
    final HistoryService historyService;
    final SchedulerService schedulerService;
    final ModelRegistrationService modelRegistrationService;

    @Operation(
            operationId = "statusCheck",
            summary = "Status Check",
            description =
                    "모니터링의 상태를 확인하기 위한 API입니다."
    )
    @GetMapping("/statusCheck")
    public void statusCheck() {
    }

    // Services
    @Operation(
            operationId = "connectStatus",
            summary = "Connect Status",
            description =
                    "모든 서비스들의 연결 상태를 위한 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "서비스들의 연결 상태에 대한 정보",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = recordModel.ConnectStatusResponse.class)))
            })
    @GetMapping("/connectStatus")
    public recordModel.ConnectStatusResponse connectStatus() {
        return recordModel.ConnectStatusResponse.builder()
                .total(servicesService.getServicesCount())
                .connected(servicesService.countByConnectionStatusIsTrue())
                .disConnected(servicesService.countByConnectionStatusIsFalse())
                .build();
    }

    @Operation(
            operationId = "targetConnectStatus",
            summary = "Target Connect Status",
            description =
                    "특정 서비스의 연결 상태를 위한 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 서비스의 연결 상태에 대한 히스토리 정보",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Services.class)))
            })
    @GetMapping("/connectStatus/{serviceID}")
    public Services connectStatus(
            @Parameter(description = "연결 상태 히스토리를 얻을 서비스의 아이디",
                    schema = @Schema(type = "string"))
            @PathVariable("serviceID") String serviceID,
            @Parameter(description = "요청된 데이터의 페이지 번호를 위한 매개변수",
                    schema = @Schema(type = "int", example = "0"))
            @RequestParam(value = "page", required = false,
                    defaultValue = "${open-metadata.pageable_config.connect.page}") @Min(0) int page,
            @Parameter(description = "한 페이지에 표시할 데이터의 수를 나타내는 매개변수",
                    schema = @Schema(type = "int", example = "5"))
            @RequestParam(value = "size", required = false,
                    defaultValue = "${open-metadata.pageable_config.connect.size}") @Min(1) int size) {
        var serviceId = UUID.fromString(serviceID);
        var serviceOpt = servicesService.getServices(serviceId);
        var histories = historyService.getServiceConnectionHistories(serviceId, page, size);
        return serviceOpt.map(services -> services.toBuilder()
                .connects(null)
                .histories(histories)
                .build()).orElse(null);
    }

    @Operation(
            operationId = "responseTime",
            summary = "Response Time",
            description =
                    "모든 서비스들의 평균 응답 시간을 얻기 위한 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모든 서비스들의 평균 응답 시간 정보",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = ServicesConnect.class))))
            })
    @GetMapping("/responseTime")
    public List<ServicesConnect> responseTimes(
            @Parameter(description = "평균 응답 시간의 내림차순 혹은 오름차순을 정하기 위한 매개변수",
                    schema = @Schema(type = "boolean", example = "true"))
            @RequestParam(value = "orderByAsc", required = false,
                    defaultValue = "false") boolean orderBy,
            @Parameter(description = "요청된 데이터의 페이지 번호를 위한 매개변수",
                    schema = @Schema(type = "int", example = "0"))
            @RequestParam(value = "page", required = false,
                    defaultValue = "${open-metadata.pageable_config.connect.page}") @Min(0) int page,
            @Parameter(description = "한 페이지에 표시할 데이터의 수를 나타내는 매개변수",
                    schema = @Schema(type = "int", example = "5"))
            @RequestParam(value = "size", required = false,
                    defaultValue = "${open-metadata.pageable_config.connect.size}") @Min(1) int size) {
        return orderBy ?
                connectService.getServiceConnectResponseTimeAscList(page, size) :
                connectService.getServiceConnectResponseTimeDescList(page, size);
    }

    @Operation(
            operationId = "targetResponseTime",
            summary = "Target Response Time",
            description =
                    "특정 서비스의 응답 시간 히스토리를 얻기 위한 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 서비스들의 응답 시간 히스토리 정보",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = ServicesConnect.class))))
            })
    @GetMapping("/responseTime/{serviceID}")
    public List<ServicesConnect> targetResponseTimes(
            @Parameter(description = "응답 시간 히스토리를 얻을 특정 서비스의 아이디",
                    schema = @Schema(type = "string"))
            @PathVariable("serviceID") String serviceID,
            @Parameter(description = "요청된 데이터의 페이지 번호를 위한 매개변수",
                    schema = @Schema(type = "int", example = "0"))
            @RequestParam(value = "page", required = false,
                    defaultValue = "${open-metadata.pageable_config.connect.page}") int page,
            @Parameter(description = "한 페이지에 표시할 데이터의 수를 나타내는 매개변수",
                    schema = @Schema(type = "int", example = "5"))
            @RequestParam(value = "size", required = false,
                    defaultValue = "${open-metadata.pageable_config.connect.size}") int size
    ) {
        var serviceId = UUID.fromString(serviceID);
        return connectService.getServiceConnectResponseTime(serviceId, page, size);
    }

    @Operation(
            operationId = "eventHistory",
            summary = "Event History",
            description =
                    "히스토리를 위한 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "히스토리 정보",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Services.class))))
            })
    @GetMapping("/eventHistory")
    public List<Services> eventHistory(
            @Parameter(description = "한 페이지에 표시할 데이터의 수를 나타내는 매개변수",
                    schema = @Schema(type = "int", example = "5"))
            @RequestParam(value = "size", required = false,
                    defaultValue = "${open-metadata.pageable_config.history.size}") @Min(1) int size) {
        var eventHistories = historyService.getServiceHistories(size);
        List<Services> servicesList = new ArrayList<>();
        for (var eventHistory : eventHistories) {
            var targetServiceOpt = servicesService.getServices(eventHistory.getServiceID());
            List<ServicesHistory> events = new ArrayList<>();
            events.add(eventHistory);
            if (targetServiceOpt.isPresent()) {
                var targetService = targetServiceOpt.get().toBuilder()
                        .connects(null)
                        .histories(events)
                        .build();
                servicesList.add(targetService);
            }
        }

        return servicesList;
    }

    @Operation(
            operationId = "targetEventHistory",
            summary = "Tagret Event History",
            description =
                    "특정 서비스의 히스토리를 위한 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 서비스의 히스토리 정보",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Services.class)))
            })
    @GetMapping("/eventHistory/{serviceID}")
    public Services eventHistory(
            @Parameter(description = "히스토리를 얻을 특정 서비스의 아이디",
                    schema = @Schema(type = "string"))
            @PathVariable("serviceID") String serviceID,
            @Parameter(description = "요청된 데이터의 페이지 번호를 위한 매개변수",
                    schema = @Schema(type = "int", example = "0"))
            @RequestParam(value = "page", required = false,
                    defaultValue = "${open-metadata.pageable_config.history.page}") @Min(0) int page,
            @Parameter(description = "한 페이지에 표시할 데이터의 수를 나타내는 매개변수",
                    schema = @Schema(type = "int", example = "5"))
            @RequestParam(value = "size", required = false,
                    defaultValue = "${open-metadata.pageable_config.history.size}") @Min(1) int size
    ) {
        var eventHistories = historyService.getServiceHistories(UUID.fromString(serviceID), page, size);
        var targetServiceOpt = servicesService.getServices(UUID.fromString(serviceID));
        return targetServiceOpt.map(services -> services.toBuilder()
                .connects(null)
                .histories(eventHistories)
                .build()).orElse(null);
    }

    @Operation(
            operationId = "model",
            summary = "Ranking of Voted Model",
            description =
                    "데이터 모델의 추천 순위를 위한 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "데이터 모델의 추천 순위 정보",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Services.class))))
            })
    @GetMapping("/models")
    public List<ModelRegistration> models(
            @Parameter(description = "한 페이지에 표시할 데이터의 수를 나타내는 매개변수",
                    schema = @Schema(type = "int", example = "5"))
            @RequestParam(value = "size", required = false) @Min(1) int size
    ) {
        return modelRegistrationService.getModelRegistrations(size);
    }

    @Operation(
            operationId = "Schedule",
            summary = "Manual Schedule",
            description =
                    "Monitoring 정보 수집을 위해 자동적으로 돌아가는 Schduler를 수동으로 돌리기 위한 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            })
    @GetMapping("/runSchedule")
    public void runSchedule(
            @Parameter(description = "스케쥴링을 시도한 사용자 이름에 대한 매개변수",
                    schema = @Schema(type = "string", example = "admin"))
            @RequestParam(value = "userName", required = true) String userName
    ) {
        schedulerService.collectDataByUser(userName);
        schedulerService.saveData();
    }

    @GetMapping("/Test")
    public void test() {
        schedulerService.collectDataByUser("testUser");
    }
}


