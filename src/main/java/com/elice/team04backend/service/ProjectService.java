package com.elice.team04backend.service;

import com.elice.team04backend.dto.project.*;
import com.elice.team04backend.dto.search.ProjectSearchCondition;


public interface ProjectService {
    ProjectTotalResponseDto getProjectsByUser(Long userId, int page, int size);
    ProjectSearchResponseDto getProjectByCondition(Long userId, ProjectSearchCondition condition, int page, int size);
    ProjectResponseDto getProjectById(Long projectId);
    ProjectResponseDto postProject(Long userId, ProjectRequestDto projectRequestDto);
    ProjectResponseDto patchProject(Long userId, Long projectId, ProjectUpdateDto projectUpdateDto);
    void deleteProject(Long userId, Long projectId);

    // 초대 관련
    //void inviteUsers(Long projectId, List<String> emails);
    ProjectUserInfoDto inviteSingleUsers(Long projectId, String email);
    void acceptInvitation(String token);
    void leaveProject(Long userId, Long projectId, Long newManagerId);
    void assignManager(Long currentManagerId, Long projectId, Long newManagerId);
}
