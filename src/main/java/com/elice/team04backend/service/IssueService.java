package com.elice.team04backend.service;

import com.elice.team04backend.dto.issue.IssueRequestDto;
import com.elice.team04backend.dto.issue.IssueResponseDto;
import com.elice.team04backend.dto.issue.IssueUpdateDto;

import java.util.List;

public interface IssueService {
    IssueResponseDto postIssue(Long postId, IssueRequestDto issueRequestDto);
    List<IssueResponseDto> getIssueByProjectId(Long projectId);
    IssueResponseDto patchIssue(Long issueId, IssueUpdateDto issueUpdateDto);
    void deleteIssue(Long issueId);
}
