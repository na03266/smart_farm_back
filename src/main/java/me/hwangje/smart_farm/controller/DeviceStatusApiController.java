package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.DeviceStatus;
import me.hwangje.smart_farm.dto.DeviceStatusDto.DeviceStatusResponse;
import me.hwangje.smart_farm.dto.DeviceStatusDto.UpdateDeviceStatusRequest;
import me.hwangje.smart_farm.service.DeviceStatusService;
import me.hwangje.smart_farm.service.MqttPublishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/device-statuses")
@Tag(name = "DeviceStatus", description = "디바이스 상태 관련 API")
public class DeviceStatusApiController {

    private final DeviceStatusService deviceStatusService;
    private final MqttPublishService mqttPublishService;

    @Operation(summary = "모든 디바이스 상태 조회", description = "특정 컨트롤러의 모든 디바이스 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{controllerId}")
    public ResponseEntity<List<DeviceStatusResponse>> findAllDeviceStatuses(@PathVariable Long controllerId){
        List<DeviceStatus> deviceStatuses = deviceStatusService.findAllByControllerId(controllerId);
        List<DeviceStatusResponse> responses = deviceStatuses.stream()
                .map(DeviceStatusResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "디바이스 상태 수정", description = "특정 디바이스의 상태 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "디바이스 상태 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeviceStatusResponse> updateDeviceStatus(@PathVariable Long id, @RequestBody UpdateDeviceStatusRequest request) throws JsonProcessingException {
        DeviceStatus updatedDeviceStatus = deviceStatusService.update(id, request);
        mqttPublishService.publishDeviceStatus(updatedDeviceStatus.getController().getId());
        return ResponseEntity.ok().body(new DeviceStatusResponse(updatedDeviceStatus));
    }

}