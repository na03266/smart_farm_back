package me.hwangje.smart_farm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.dto.GroupDto.*;
import me.hwangje.smart_farm.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/groups")
@Tag(name = "Group", description = "매니저 소속 관련 API")
public class GroupApiController {
    private final GroupService groupService;

    @Operation(summary = "그룹 추가", description = "새로운 그룹을 생성, ADMIN 권한 필요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "그룹 생성 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GroupResponse> addGroup(@RequestBody AddGroupRequest request) {
        Group savedGroup = groupService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GroupResponse(savedGroup));
    }

    @Operation(summary = "모든 그룹 조회", description = "모든 그룹을 조회. ADMIN 권한 필요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<GroupResponse>> findAllGroups() {
        List<GroupResponse> groups = groupService.findAll()
                .stream()
                .map(GroupResponse::new)
                .toList();
        return ResponseEntity.ok().body(groups);
    }

    @Operation(summary = "특정 그룹 조회", description = "ID로 특정 그룹을 조회, 모든 권한 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> findGroup(@PathVariable Long id) {
        Group group = groupService.findById(id);
        return ResponseEntity.ok().body(new GroupResponse(group));
    }

    @Operation(summary = "그룹 정보 수정", description = "특정 그룹의 정보를 수정, ADMIN or MANAGER 권한 필요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> updateGroup(@PathVariable Long id, @RequestBody UpdateGroupRequest request) {
        Group updatedGroup = groupService.update(id, request);
        return ResponseEntity.ok().body(new GroupResponse(updatedGroup));
    }

    @Operation(summary = "그룹 삭제", description = "특정 그룹을 삭제, ADMIN 권한 필요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "그룹 검색", description = "그룹에 포함된 이름을 기준으로 검색, ADMIN 권한 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "검색 결과 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<GroupResponse>> searchGroups(@RequestParam String name) {
        List<Group> groups = groupService.searchGroups(name);
        if (groups.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<GroupResponse> response = groups.stream()
                .map(GroupResponse::new)
                .toList();
        return ResponseEntity.ok().body(response);
    }
}