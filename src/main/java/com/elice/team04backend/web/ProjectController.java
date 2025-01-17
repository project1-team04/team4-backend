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
public class ProjectController {

    private final ProjectService projectService;
    private final LabelService labelService;

    /**
     * TODO
     * 유저 아이디로 project 찾는 메서드 추가
     */

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/test")
    public ResponseEntity testProject(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Long projectId) {
        log.info("{}",userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getUserProjects(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ProjectResponseDto> projectResponseDtos = projectService.getProjectsByUser(userDetails.getUserId(), page, size);
        return ResponseEntity.ok(projectResponseDtos);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProject(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long projectId) {
        ProjectResponseDto projectResponseDto = projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectResponseDto);
    }


//    @GetMapping
//    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
//        List<ProjectResponseDto> projectResponseDtos = projectService.getAllProjects();
//        return ResponseEntity.ok(projectResponseDtos);
//    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> postProject(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
        ProjectResponseDto projectResponseDto = projectService.postProject(projectRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponseDto);
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> patchProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        ProjectResponseDto projectResponseDto = projectService.patchProject(projectId, projectUpdateDto);
        return ResponseEntity.ok(projectResponseDto);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }


    //라벨 관련 컨트롤러

    @PostMapping("/{projectId}/labels")
    public ResponseEntity<LabelResponseDto> postLabel(
            @PathVariable Long projectId,
            @Valid @RequestBody LabelRequestDto labelRequestDto) {
        LabelResponseDto labelResponseDto = labelService.postLabel(projectId, labelRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(labelResponseDto);
    }

    @GetMapping("/{projectId}/labels")
    public ResponseEntity<List<LabelResponseDto>> getAllLabelsByProjectId(@PathVariable Long projectId) {
        List<LabelResponseDto> labelResponseDtos = labelService.getAllLabelsByProjectId(projectId);
        return ResponseEntity.ok(labelResponseDtos);
    }

    @PatchMapping("/{projectId}/labels/{labelId}")
    public ResponseEntity<LabelResponseDto> patchLabel(
            @PathVariable Long labelId,
            @Valid @RequestBody LabelUpdateDto labelUpdateDto) {
        LabelResponseDto labelResponseDto = labelService.patchLabel(labelId, labelUpdateDto);
        return ResponseEntity.ok(labelResponseDto);
    }

    @DeleteMapping("/{projectId}/labels/{labelId}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long labelId) {
        labelService.deleteLabel(labelId);
        return ResponseEntity.noContent().build();
    }

}
