package com.elice.team04backend.repository.querydsl;

import com.elice.team04backend.dto.search.ProjectSearchCondition;
import com.elice.team04backend.entity.Project;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.elice.team04backend.entity.QProject.project;
import static com.elice.team04backend.entity.QUserProjectRole.userProjectRole;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Project> searchProjects(Long userId, ProjectSearchCondition searchCondition, Pageable pageable) {
        List<Project> projects = queryFactory
                .selectFrom(project)
                .leftJoin(project.userProjectRoles, userProjectRole).fetchJoin()
                .where(
                        userIdEq(userId),
                        conditionContains(searchCondition.getCondition())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory
                .select(project.count())
                .from(project)
                .leftJoin(project.userProjectRoles, userProjectRole)
                .where(
                        userIdEq(userId),
                        conditionContains(searchCondition.getCondition())
                )
                .fetchOne()
        ).orElse(0L);
        return new PageImpl<>(projects, pageable, total);
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
