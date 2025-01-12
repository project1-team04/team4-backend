package com.elice.team04backend.service;

import com.elice.team04backend.dto.Project.ProjectRequestDto;
import com.elice.team04backend.dto.Project.ProjectResponseDto;
import com.elice.team04backend.dto.Project.ProjectUpdateDto;

public interface ProjectService {
    ProjectResponseDto postProject(ProjectRequestDto projectRequestDto);
    void patchProject(ProjectUpdateDto projectUpdateDto);
}
