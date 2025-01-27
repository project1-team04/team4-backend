package com.elice.team04backend.web;

import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.dto.label.LabelRequestDto;
import com.elice.team04backend.dto.label.LabelResponseDto;
import com.elice.team04backend.dto.label.LabelUpdateDto;
import com.elice.team04backend.dto.project.*;
import com.elice.team04backend.dto.search.ProjectSearchCondition;
import com.elice.team04backend.dto.userProjectRole.UserProjectRoleResponseDto;
import com.elice.team04backend.service.LabelService;
import com.elice.team04backend.service.ProjectService;
import com.elice.team04backend.service.UserProjectRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project 관리", description = "Project 관련 API 입니다.")
public class ProjectController {

    private final ProjectService projectService;
    private final LabelService labelService;
    private final UserProjectRoleService userProjectRoleService;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/test")
    public ResponseEntity testProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId) {
        log.info("{}",userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 유저와 관련된 프로젝트 조건 검색", description = "로그인 유저와 관련된 모든 프로젝트를 조건에 따라 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ProjectSearchResponseDto> getProjectByCondition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "condition", required = false) String condition) {
        ProjectSearchCondition searchCondition = new ProjectSearchCondition(condition);
        ProjectSearchResponseDto projectSearchResponseDto = projectService.getProjectByCondition(userDetails.getUserId(), searchCondition, page, size);
        return ResponseEntity.ok(projectSearchResponseDto);
    }

    @Operation(summary = "로그인 유저와 관련된 프로젝트 조회", description = "로그인 유저와 관련된 모든 프로젝트를 조회합니다.")
    @GetMapping
    public ResponseEntity<ProjectTotalResponseDto> getUserProjects(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        ProjectTotalResponseDto projectResponseDto = projectService.getProjectsByUser(userDetails.getUserId(), page, size);
        return ResponseEntity.ok(projectResponseDto);
    }

    @Operation(summary = "프로젝트에 관련된 모든 유저를 조회", description = "프로젝트에 관련된 모든 유저들을 조회합니다.")
    @GetMapping("/users")
    public ResponseEntity<List<UserProjectRoleResponseDto>> getUsersByProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId) {
        List<UserProjectRoleResponseDto> users = userProjectRoleService.getUsersByProjectId(projectId);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "프로젝트 작성", description = "프로젝트를 작성합니다.")
    @PostMapping
    public ResponseEntity<ProjectResponseDto> postProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ProjectRequestDto projectRequestDto) {
        ProjectResponseDto projectResponseDto = projectService.postProject(userDetails.getUserId(), projectRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponseDto);
    }

    @Operation(summary = "단일 프로젝트 조회", description = "프로젝트 id로 단일 프로젝트를 조회합니다.")
    @GetMapping("/details")
    public ResponseEntity<ProjectResponseDto> getProjectDetails(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId) {
        ProjectResponseDto projectResponseDto = projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectResponseDto);
    }

    @Operation(summary = "단일 프로젝트 수정", description = "단일 프로젝트를 수정합니다.")
    @PatchMapping
    public ResponseEntity<ProjectResponseDto> patchProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId,
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        ProjectResponseDto projectResponseDto = projectService.patchProject(userDetails.getUserId(), projectId, projectUpdateDto);
        return ResponseEntity.ok(projectResponseDto);
    }

    @Operation(summary = "단일 프로젝트 삭제", description = "단일 프로젝트를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId) {
        projectService.deleteProject(userDetails.getUserId(), projectId);
        return ResponseEntity.noContent().build();
    }

    //라벨 관련 컨트롤러

    @Operation(summary = "라벨 추가", description = "특정 프로젝트에 라벨을 추가합니다.")
    @PostMapping("/labels")
    public ResponseEntity<LabelResponseDto> postLabel(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId,
            @Valid @RequestBody LabelRequestDto labelRequestDto) {
        LabelResponseDto labelResponseDto = labelService.postLabel(projectId, labelRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(labelResponseDto);
    }

    @Operation(summary = "라벨 조회", description = "특정 프로젝트의 모든 라벨을 조회합니다.")
    @GetMapping("/labels")
    public ResponseEntity<List<LabelResponseDto>> getAllLabelsByProjectId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId) {
        List<LabelResponseDto> labelResponseDtos = labelService.getAllLabelsByProjectId(projectId);
        return ResponseEntity.ok(labelResponseDtos);
    }

    @Operation(summary = "라벨 수정", description = "특정 프로젝트의 라벨을 수정합니다.")
    @PatchMapping("/labels")
    public ResponseEntity<LabelResponseDto> patchLabel(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("labelId") Long labelId,
            @Valid @RequestBody LabelUpdateDto labelUpdateDto) {
        LabelResponseDto labelResponseDto = labelService.patchLabel(labelId, labelUpdateDto);
        return ResponseEntity.ok(labelResponseDto);
    }

    @Operation(summary = "라벨 삭제", description = "특정 프로젝트의 라벨을 삭제합니다.")
    @DeleteMapping("/labels")
    public ResponseEntity<Void> deleteLabel(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("labelId") Long labelId) {
        labelService.deleteLabel(labelId);
        return ResponseEntity.noContent().build();
    }

    // 초대 및 탈퇴 관련 컨트롤러

    @Operation(summary = "프로젝트에 유저 초대", description = "프로젝트에 유저를 초대합니다.")
    @PostMapping("/invite")
    public ResponseEntity<Void> inviteUsers(
            @RequestParam("projectId") Long projectId,
            @Valid @RequestBody ProjectInviteRequestDto projectInviteRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        projectService.inviteUsers(projectId, projectInviteRequestDto.getEmails());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로젝트 탈퇴", description = "유저가 프로젝트에서 탈퇴합니다.")
    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId,
            @RequestParam(name = "newManagerId", required = false) Long newManagerId) {
        projectService.leaveProject(userDetails.getUserId(), projectId, newManagerId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "프로젝트 관리자를 변경", description = "MANAGER가 특정 유저에게 MANAGER 권한을 부여합니다.")
    @PatchMapping("/assign-manager")
    public ResponseEntity<Void> assignManager(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("projectId") Long projectId,
            @RequestParam("newManagerId") Long newManagerId) {
        projectService.assignManager(userDetails.getUserId(), projectId, newManagerId);
        return ResponseEntity.ok().build();
    }

}
