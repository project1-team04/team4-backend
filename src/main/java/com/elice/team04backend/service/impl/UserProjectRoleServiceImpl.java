package com.elice.team04backend.service.impl;

import com.elice.team04backend.entity.UserProjectRole;
import com.elice.team04backend.repository.UserProjectRoleRepository;
import com.elice.team04backend.service.UserProjectRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProjectRoleServiceImpl implements UserProjectRoleService {

    private final UserProjectRoleRepository userProjectRoleRepository;

    @Override
    public UserProjectRole getUserRoleForProject(Long userId, Long projectId) {
        return userProjectRoleRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new IllegalStateException(""));
    }
}
