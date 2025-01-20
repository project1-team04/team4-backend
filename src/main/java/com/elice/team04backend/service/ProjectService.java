package com.elice.team04backend.service;

import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;

import java.util.List;

public interface ProjectService {
    List<ProjectResponseDto> getProjectsByUser(Long userId, int page, int size);
    ProjectResponseDto getProjectById(Long projectId);
    ProjectResponseDto postProject(Long userId, ProjectRequestDto projectRequestDto, List<String> emails);
    ProjectResponseDto patchProject(Long userId, Long projectId, ProjectUpdateDto projectUpdateDto);
    void deleteProject(Long userId, Long projectId);

    void leaveProject(Long userId, Long projectId, Long newManagerId);
    // 초대 관련
    String inviteMember(String email);
}
