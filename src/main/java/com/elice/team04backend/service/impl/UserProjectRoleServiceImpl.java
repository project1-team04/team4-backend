package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.dto.project.ProjectManageResponseDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.userProjectRole.UserProjectImageResponseDto;
import com.elice.team04backend.dto.userProjectRole.UserProjectRoleResponseDto;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.entity.UserProjectRole;
import com.elice.team04backend.repository.UserProjectRoleRepository;
import com.elice.team04backend.service.UserProjectRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProjectRoleServiceImpl implements UserProjectRoleService {

    private final UserProjectRoleRepository userProjectRoleRepository;

    @Override
    public UserProjectRole getUserRoleForProject(Long userId, Long projectId) {
        return userProjectRoleRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new IllegalStateException(""));
    }

    @Override
    public List<UserProjectImageResponseDto> getUsersByProjectId(Long projectId) {
        List<UserProjectRole> userProjectRoles = userProjectRoleRepository.findAllByProjectId(projectId);
        if (userProjectRoles.isEmpty()) {
            throw new CustomException(ErrorCode.PROJECT_USERS_NOT_FOUND);
        }
        return userProjectRoles.stream()
                .map(userProjectRole -> UserProjectImageResponseDto.builder()
                        .userId(userProjectRole.getUser().getId())
                        .userName(userProjectRole.getUser().getUsername())
                        .email(userProjectRole.getUser().getEmail())
                        .role(userProjectRole.getRole())
                        .imgUrl(userProjectRole.getUser().getProfileImage())
                        .build())
                .toList();
    }

    @Override
    public List<ProjectManageResponseDto> getManagedProjects(Long userId) {
        List<Project> managedProjects = userProjectRoleRepository.findProjectsByUserIdRoleManager(userId);

        if (managedProjects.isEmpty()) {
            throw new CustomException(ErrorCode.PROJECT_NOT_FOUND);
        }

        return managedProjects.stream()
                .map(project -> {
                    List<UserProjectRole> members = userProjectRoleRepository.findAllUserByProjectIdRoleMember(project.getId());

                    if (members.isEmpty()) {
                        return null;
                    }

                    List<UserProjectRoleResponseDto> memberDtos = members.stream()
                            .map(userProjectRole -> new UserProjectRoleResponseDto(
                                    userProjectRole.getUser().getId(),
                                    userProjectRole.getUser().getUsername(),
                                    userProjectRole.getUser().getEmail(),
                                    userProjectRole.getRole()))
                            .toList();

                    return new ProjectManageResponseDto(
                            new ProjectResponseDto(
                                    String.valueOf(project.getId()),
                                    project.getProjectKey(),
                                    project.getName(),
                                    project.getIssueCount()
                            ),
                            memberDtos
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
