package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.dto.userProjectRole.UserProjectRoleResponseDto;
import com.elice.team04backend.entity.UserProjectRole;
import com.elice.team04backend.repository.UserProjectRoleRepository;
import com.elice.team04backend.service.UserProjectRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<UserProjectRoleResponseDto> getUsersByProjectId(Long projectId) {
        List<UserProjectRole> userProjectRoles = userProjectRoleRepository.findAllByProjectId(projectId);
        if (userProjectRoles.isEmpty()) {
            throw new CustomException(ErrorCode.PROJECT_USERS_NOT_FOUND);
        }

        return userProjectRoles.stream()
                .map(userProjectRole -> UserProjectRoleResponseDto.builder()
                        .userId(userProjectRole.getUser().getId())
                        .userName(userProjectRole.getUser().getUsername())
                        .email(userProjectRole.getUser().getEmail())
                        .role(userProjectRole.getRole())
                        .build())
                .toList();
    }
}
