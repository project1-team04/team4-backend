package com.elice.team04backend.service;

import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;

import java.util.List;

public interface ProjectService {
    ProjectResponseDto getProject(Long projectId);
    List<ProjectResponseDto> getAllProjects();
    ProjectResponseDto postProject(ProjectRequestDto projectRequestDto);
    ProjectResponseDto patchProject(ProjectUpdateDto projectUpdateDto);
    void deleteProject(Long projectId);
}
