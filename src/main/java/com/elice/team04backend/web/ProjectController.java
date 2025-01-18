package com.elice.team04backend.web;

import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.dto.label.LabelRequestDto;
import com.elice.team04backend.dto.label.LabelResponseDto;
import com.elice.team04backend.dto.label.LabelUpdateDto;
import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
import com.elice.team04backend.service.LabelService;
import com.elice.team04backend.service.ProjectService;
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


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/test")
    public ResponseEntity testProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long projectId) {
        log.info("{}",userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 유저와 관련된 프로젝트 조회", description = "로그인 유저와 관련된 모든 프로젝트를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getUserProjects(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ProjectResponseDto> projectResponseDtos = projectService.getProjectsByUser(userDetails.getUserId(), page, size);
        return ResponseEntity.ok(projectResponseDtos);
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
            @RequestParam Long projectId) {
        ProjectResponseDto projectResponseDto = projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectResponseDto);
    }

    @Operation(summary = "단일 프로젝트 수정", description = "단일 프로젝트를 수정합니다.")
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping
    public ResponseEntity<ProjectResponseDto> patchProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long projectId,
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        ProjectResponseDto projectResponseDto = projectService.patchProject(userDetails.getUserId(), projectId, projectUpdateDto);
        return ResponseEntity.ok(projectResponseDto);
    }

    @Operation(summary = "단일 프로젝트 삭제", description = "단일 프로젝트를 삭제합니다.")
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long projectId) {
        projectService.deleteProject(userDetails.getUserId(), projectId);
        return ResponseEntity.noContent().build();
    }

    //라벨 관련 컨트롤러

    @Operation(summary = "라벨 추가", description = "특정 프로젝트에 라벨을 추가합니다.")
    @PostMapping("/labels")
    public ResponseEntity<LabelResponseDto> postLabel(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long projectId,
            @Valid @RequestBody LabelRequestDto labelRequestDto) {
        LabelResponseDto labelResponseDto = labelService.postLabel(projectId, labelRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(labelResponseDto);
    }

    @Operation(summary = "라벨 조회", description = "특정 프로젝트의 모든 라벨을 조회합니다.")
    @GetMapping("/labels")
    public ResponseEntity<List<LabelResponseDto>> getAllLabelsByProjectId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long projectId) {
        List<LabelResponseDto> labelResponseDtos = labelService.getAllLabelsByProjectId(projectId);
        return ResponseEntity.ok(labelResponseDtos);
    }

    @Operation(summary = "라벨 수정", description = "특정 프로젝트의 라벨을 수정합니다.")
    @PatchMapping("/labels")
    public ResponseEntity<LabelResponseDto> patchLabel(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            //@RequestParam Long projectId,
            @RequestParam Long labelId,
            @Valid @RequestBody LabelUpdateDto labelUpdateDto) {
        LabelResponseDto labelResponseDto = labelService.patchLabel(labelId, labelUpdateDto);
        return ResponseEntity.ok(labelResponseDto);
    }

    @Operation(summary = "라벨 삭제", description = "특정 프로젝트의 라벨을 삭제합니다.")
    @DeleteMapping("/labels")
    public ResponseEntity<Void> deleteLabel(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long labelId) {
        labelService.deleteLabel(labelId);
        return ResponseEntity.noContent().build();
    }

}
