package com.elice.team04backend.repository.querydsl;

import com.elice.team04backend.dto.search.IssueSearchCondition;
import com.elice.team04backend.entity.Issue;

import java.util.List;

public interface IssueRepositoryCustom {
    List<Issue> searchIssues(Long projectId, IssueSearchCondition issueSearchCondition);
}
