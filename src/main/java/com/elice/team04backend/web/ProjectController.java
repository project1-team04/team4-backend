package com.elice.team04backend.web;

import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
import com.elice.team04backend.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * TODO
     * 유저 아이디로 project 찾는 메서드 추가
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> projectResponseDtos = projectService.getAllProjects();
        return ResponseEntity.ok(projectResponseDtos);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> postProject(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
        ProjectResponseDto projectResponseDto = projectService.postProject(projectRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponseDto);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProject(@PathVariable Long projectId) {
        ProjectResponseDto projectResponseDto = projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectResponseDto);
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

}
