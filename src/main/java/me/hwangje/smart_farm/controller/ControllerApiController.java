package me.hwangje.smart_farm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.dto.ControllerDto.AddControllerRequest;
import me.hwangje.smart_farm.dto.ControllerDto.ControllerResponse;
import me.hwangje.smart_farm.dto.ControllerDto.UpdateControllerRequest;
import me.hwangje.smart_farm.service.ControllerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/controllers")
@Tag(name = "Controller", description = "컨트롤러 관련 API")
public class ControllerApiController {
    private final ControllerService controllerService;

    @Operation(summary = "컨트롤러 추가", description = "새로운 컨트롤러를 생성, ADMIN 권한 필요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "컨트롤러 생성 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ControllerResponse> addController(@RequestBody AddControllerRequest request) {
        Controller savedController = controllerService.save(request);

        controllerService.createDefaultDeviceTimers(savedController);
        controllerService.createDefaultDeviceSetups(savedController);
        controllerService.createDefaultSensorSetup(savedController);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ControllerResponse(savedController));
    }

    @Operation(summary = "할당된 모든 컨트롤러 조회", description = "모든 컨트롤러를 조회, 권한에 따라 결과가 다름.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping
    public ResponseEntity<List<ControllerResponse>> findAllControllersByRole(
            @RequestParam(required = false) String controllerNameLike,
            @RequestParam(required = false) String userNameLike,
            @RequestParam(required = false) String groupNameLike
    ) {
        List<ControllerResponse> controllers = controllerService.findAllByRole(
                        controllerNameLike, userNameLike, groupNameLike)
                .stream()
                .map(ControllerResponse::new)
                .toList();
        return ResponseEntity.ok().body(controllers);
    }

    @Operation(summary = "특정 컨트롤러 조회", description = "ID로 특정 컨트롤러를 조회, 모든 권한 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "컨트롤러를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ControllerResponse> findController(@PathVariable Long id) {
        Controller controller = controllerService.findById(id);
        return ResponseEntity.ok().body(new ControllerResponse(controller));
    }

    @Operation(summary = "컨트롤러 정보 수정", description = "특정 컨트롤러의 정보를 수정, ADMIN or MANAGER 권한 필요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "컨트롤러를 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ControllerResponse> updateController(@PathVariable Long id, @RequestBody UpdateControllerRequest request) {
        Controller updatedController = controllerService.update(id, request);
        return ResponseEntity.ok().body(new ControllerResponse(updatedController));
    }

    @Operation(summary = "컨트롤러 삭제", description = "특정 컨트롤러를 삭제, ADMIN 권한 필요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "컨트롤러를 찾을 수 없음")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteController(@PathVariable Long id) {
        controllerService.delete(id);
        return ResponseEntity.ok().build();
    }

}
