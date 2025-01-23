package com.elice.team04backend.repository.querydsl;

import com.elice.team04backend.dto.search.ProjectSearchCondition;
import com.elice.team04backend.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    Page<Project> searchProjects(Long userId, ProjectSearchCondition projectSearchCondition, Pageable pageable);
}
