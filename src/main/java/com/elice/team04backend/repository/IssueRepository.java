package com.elice.team04backend.repository;

import com.elice.team04backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByProjectId(Long projectId);
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.issueKey, LENGTH(:projectKey) + 2) AS int)), 0) " +
            "FROM Issue i WHERE i.project.id = :projectId")
    int findMaxIssueIndexByProject(@Param("projectId") Long projectId, @Param("projectKey") String projectKey);
}
