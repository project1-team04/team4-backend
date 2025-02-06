package com.elice.team04backend.service;

import com.elice.team04backend.dto.project.*;
import com.elice.team04backend.dto.search.ProjectSearchCondition;

import java.util.List;


public interface ProjectService {
    ProjectTotalResponseDto getProjectsByUser(Long userId, int page, int size);
    ProjectTotalResponseDto getProjectsByUserInternal(Long userId, int page, int size);
    ProjectSearchResponseDto getProjectByCondition(Long userId, ProjectSearchCondition condition, int page, int size);
    ProjectResponseDto getProjectById(Long projectId);
    ProjectResponseDto postProject(Long userId, ProjectRequestDto projectRequestDto);
    ProjectResponseDto patchProject(Long userId, Long projectId, ProjectUpdateDto projectUpdateDto);
    ProjectResponseDto patchProjectInternal(Long userId, Long projectId, int currentPage, int pageSize, ProjectUpdateDto projectUpdateDto);
    void deleteProject(Long userId, Long projectId);


    // 초대 관련
    ProjectUserInfoDto inviteSingleUsers(Long projectId, String email);
    void acceptInvitation(String token);
    void leaveProject(Long userId, Long projectId, Long newManagerId);
    void assignManager(Long currentManagerId, List<ProjectAssignRequestDto> assignRequestDtos);
}
