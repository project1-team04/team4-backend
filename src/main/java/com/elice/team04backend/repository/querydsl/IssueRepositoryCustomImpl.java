package com.elice.team04backend.repository.querydsl;

import com.elice.team04backend.dto.search.IssueSearchCondition;
import com.elice.team04backend.entity.Issue;
import com.elice.team04backend.entity.QIssue;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.elice.team04backend.entity.QIssue.issue;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class IssueRepositoryCustomImpl implements IssueRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Issue> searchIssues(Long projectId, IssueSearchCondition searchCondition) {
        return queryFactory
                .selectFrom(issue)
                .where(
                        projectIdEq(projectId),
                        conditionContains(searchCondition.getCondition())
                )
                .fetch();
    }

    private BooleanExpression projectIdEq(Long projectId) {
        return projectId != null ? issue.project.id.eq(projectId) : null;
    }

    private BooleanExpression conditionContains(String condition) {
        return hasText(condition)
                ? issue.name.containsIgnoreCase(condition)
                .or(issue.issueKey.containsIgnoreCase(condition))
                :null;
    }
}
