package com.elice.team04backend.service;

import com.elice.team04backend.dto.project.ProjectManageResponseDto;
import com.elice.team04backend.dto.userProjectRole.UserProjectRoleResponseDto;
import com.elice.team04backend.entity.UserProjectRole;

import java.util.List;

public interface UserProjectRoleService {
    UserProjectRole getUserRoleForProject(Long userId, Long projectId);
    List<UserProjectRoleResponseDto> getUsersByProjectId(Long projectId);
    List<ProjectManageResponseDto> getManagedProjects(Long userId);

}
