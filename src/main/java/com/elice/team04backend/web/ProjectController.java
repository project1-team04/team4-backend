package com.elice.team04backend.web;

import com.elice.team04backend.dto.Project.ProjectRequestDto;
import com.elice.team04backend.dto.Project.ProjectResponseDto;
import com.elice.team04backend.dto.Project.ProjectUpdateDto;
import com.elice.team04backend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project 관리", description = "Project 관련 API 입니다.")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "모든 프로젝트 조회", description = "서버에 등록된 모든 프로젝트를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> postProject(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
        ProjectResponseDto projectResponseDto = projectService.postProject(projectRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProject(@PathVariable Long id) {
        ProjectResponseDto project = projectService.getProject(id);
        return ResponseEntity.ok(project);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> patchProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        projectUpdateDto.setId(id);
        ProjectResponseDto updatedProject = projectService.patchProject(projectUpdateDto);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}
