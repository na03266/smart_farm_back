package me.hwangje.smart_farm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.SensorSetup;
import me.hwangje.smart_farm.dto.SensorSetupDto.SensorSetupResponse;
import me.hwangje.smart_farm.dto.SensorSetupDto.UpdateSensorSetupRequest;
import me.hwangje.smart_farm.service.SensorSetupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sensor-setups")
@Tag(name = "SensorSetup", description = "센서 셋업 관련 API")
public class SensorSetupApiController {
    private final SensorSetupService sensorSetupService;

    @Operation(summary = "모든 센서 셋업 조회", description = "모든 센서 셋업를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{controllerId}")
    public ResponseEntity<List<SensorSetupResponse>> findAllSensorSetups(@PathVariable Long controllerId) {
        List<SensorSetup> deviceTimers = sensorSetupService.findAllByControllerId(controllerId);
        List<SensorSetupResponse> responses = deviceTimers.stream()
                .map(SensorSetupResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "센서 셋업 수정", description = "특정 센서 셋업의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "센서 셋업을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SensorSetupResponse> updateSensorSetup(@PathVariable Long id, @RequestBody UpdateSensorSetupRequest request) {
        SensorSetup updatedSensorSetup = sensorSetupService.update(id, request);
        return ResponseEntity.ok().body(new SensorSetupResponse(updatedSensorSetup));
    }
}
