package com.elice.team04backend.repository.querydsl;

import com.elice.team04backend.dto.search.ProjectSearchCondition;
import com.elice.team04backend.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectRepositoryCustom {
    List<Project> fetchProjects(Long userId, ProjectSearchCondition searchCondition, Pageable pageable);
    Long fetchProjectCount(Long userId, ProjectSearchCondition searchCondition);
}
