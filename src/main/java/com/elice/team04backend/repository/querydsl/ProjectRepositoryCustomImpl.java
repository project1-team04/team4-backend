package com.elice.team04backend.repository.querydsl;

import com.elice.team04backend.dto.search.ProjectSearchCondition;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.entity.QProject;
import com.elice.team04backend.entity.QUserProjectRole;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QProject project = QProject.project;
    QUserProjectRole userProjectRole = QUserProjectRole.userProjectRole;

    @Override
    public List<Project> fetchProjects(Long userId, ProjectSearchCondition searchCondition, Pageable pageable) {
        return queryFactory
                .selectFrom(project)
                .leftJoin(project.userProjectRoles, userProjectRole)
                .where(
                        userIdEq(userId),
                        conditionContains(searchCondition.getCondition())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Long fetchProjectCount(Long userId, ProjectSearchCondition searchCondition) {
        return Optional.ofNullable(queryFactory
                .select(project.count())
                .from(project)
                .leftJoin(project.userProjectRoles, userProjectRole)
                .where(
                        userIdEq(userId),
                        conditionContains(searchCondition.getCondition())
                )
                .fetchOne()
        ).orElse(0L);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? userProjectRole.user.id.eq(userId) : null;
    }

    private BooleanExpression conditionContains(String condition) {
        return hasText(condition)
                ? project.name.containsIgnoreCase(condition)
                .or(project.projectKey.containsIgnoreCase(condition))
                :null;
    }
}
