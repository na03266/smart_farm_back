package me.hwangje.smart_farm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.SensorData;
import me.hwangje.smart_farm.dto.SensorDataDto.*;
import me.hwangje.smart_farm.service.SensorDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sensor-data")
@Tag(name = "SensorData", description = "센서 데이터 관련 API")
public class SensorDataApiController {
    private final SensorDataService sensorDataService;

    @Operation(summary = "최근 센서 데이터 조회", description = "특정 컨트롤러의 최근 센서 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/latest/{controllerId}")
    public ResponseEntity<List<SensorDataResponse>> getLatestSensorData(@PathVariable Long controllerId) {
        List<SensorData> latestData = sensorDataService.findLatestByControllerId(controllerId);
        List<SensorDataResponse> responses = latestData.stream()
                .map(SensorDataResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "30분 단위 평균 센서 데이터 조회", description = "특정 기간 동안의 30분 단위 평균 센서 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/average/{controllerId}")
    public ResponseEntity<List<SensorDataAverageResponse>> getAverageSensorData(
            @PathVariable Long controllerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<SensorDataAverageResponse> averages = sensorDataService.findAveragesByControllerIdAndTimeRange(controllerId, startTime, endTime);
        return ResponseEntity.ok().body(averages);
    }

    @Operation(summary = "일일 평균 센서 데이터 조회", description = "특정 기간 동안의 일일 평균 센서 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/daily-average/{controllerId}")
    public ResponseEntity<List<SensorDataAverageResponse>> getDailyAverageSensorData(
            @PathVariable Long controllerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate) {
        List<SensorDataAverageResponse> dailyAverages = sensorDataService.findDailyAveragesByControllerIdAndDateRange(controllerId, startDate, endDate);
        return ResponseEntity.ok().body(dailyAverages);
    }

    @Operation(summary = "센서 데이터 삭제", description = "특정 센서 데이터를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "센서 데이터를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensorData(@PathVariable Long id) {
        sensorDataService.delete(id);
        return ResponseEntity.ok().build();
    }
}