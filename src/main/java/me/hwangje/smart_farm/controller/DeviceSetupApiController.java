package me.hwangje.smart_farm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.DeviceSetup;
import me.hwangje.smart_farm.dto.DeviceSetupDto.DeviceSetupResponse;
import me.hwangje.smart_farm.dto.DeviceSetupDto.UpdateDeviceSetupRequest;
import me.hwangje.smart_farm.service.DeviceSetupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/device-setups")
@Tag(name = "DeviceSetup", description = "디바이스 셋업 관련 API")
public class DeviceSetupApiController {

    private final DeviceSetupService deviceSetupService;

    @Operation(summary = "모든 디바이스 셋업 조회", description = "모든 디바이스 셋업을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{controllerId}")
    public ResponseEntity<List<DeviceSetupResponse>> findAllDeviceSetups(@PathVariable Long controllerId){
        List<DeviceSetup> deviceTimers = deviceSetupService.findAllByControllerId(controllerId);
        List<DeviceSetupResponse> responses = deviceTimers.stream()
                .map(DeviceSetupResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "디바이스 셋업 수정", description = "특정 디바이스 셋업 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "디바이스 셋업 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeviceSetupResponse> updateDeviceTimer(@PathVariable Long id, @RequestBody UpdateDeviceSetupRequest request) {
        DeviceSetup updatedDeviceSetup = deviceSetupService.update(id, request);
        return ResponseEntity.ok().body(new DeviceSetupResponse(updatedDeviceSetup));
    }
}
