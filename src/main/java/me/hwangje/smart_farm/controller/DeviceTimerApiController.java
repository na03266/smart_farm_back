package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.DeviceTimer;
import me.hwangje.smart_farm.dto.DeviceTimerDto.DeviceTimerResponse;
import me.hwangje.smart_farm.dto.DeviceTimerDto.UpdateDeviceTimerRequest;
import me.hwangje.smart_farm.service.DeviceTimerService;
import me.hwangje.smart_farm.service.MqttPublishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/device-timers")
@Tag(name = "DeviceTimer", description = "디바이스 타이머 관련 API")
public class DeviceTimerApiController {
    private final DeviceTimerService deviceTimerService;
    private final MqttPublishService mqttPublishService;

    @Operation(summary = "모든 디바이스 타이머 조회", description = "모든 디바이스 타이머를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{controllerId}")
    public ResponseEntity<List<DeviceTimerResponse>> findAllDeviceTimers(@PathVariable Long controllerId) {
        List<DeviceTimer> deviceTimers = deviceTimerService.findAllByControllerId(controllerId);
        List<DeviceTimerResponse> responses = deviceTimers.stream()
                .map(DeviceTimerResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "디바이스 타이머 수정", description = "특정 디바이스 타이머의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "디바이스 타이머를 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeviceTimerResponse> updateDeviceTimer(@PathVariable Long id, @RequestBody UpdateDeviceTimerRequest request) throws JsonProcessingException {
        DeviceTimer updatedDeviceTimer = deviceTimerService.update(id, request);
        mqttPublishService.publishSetup(updatedDeviceTimer.getControllerId());

        return ResponseEntity.ok().body(new DeviceTimerResponse(updatedDeviceTimer));
    }
}