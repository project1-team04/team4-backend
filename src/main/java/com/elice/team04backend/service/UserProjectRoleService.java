package com.elice.team04backend.service;

import com.elice.team04backend.entity.UserProjectRole;

public interface UserProjectRoleService {
    UserProjectRole getUserRoleForProject(Long userId, Long projectId);
}
