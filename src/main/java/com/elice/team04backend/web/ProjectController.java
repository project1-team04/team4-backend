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
