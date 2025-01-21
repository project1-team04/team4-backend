package com.elice.team04backend.service;

import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;

import java.util.List;

public interface ProjectService {
    List<ProjectResponseDto> getProjectsByUser(Long userId, int page, int size);
    ProjectResponseDto getProjectById(Long projectId);
    ProjectResponseDto postProject(Long userId, ProjectRequestDto projectRequestDto);
    ProjectResponseDto patchProject(Long userId, Long projectId, ProjectUpdateDto projectUpdateDto);
    void deleteProject(Long userId, Long projectId);

    // 초대 관련
    void inviteUserToProject(Long managerId, Long projectId, String email);
    void acceptInvitation(String token);
    void leaveProject(Long userId, Long projectId, Long newManagerId);
    void assignManager(Long currentManagerId, Long projectId, Long newManagerId);
}
