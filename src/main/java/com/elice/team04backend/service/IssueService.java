package com.elice.team04backend.service;

import com.elice.team04backend.dto.issue.IssueRequestDto;
import com.elice.team04backend.dto.issue.IssueResponseDto;
import com.elice.team04backend.dto.issue.IssueUpdateDto;
import com.elice.team04backend.dto.search.IssueSearchCondition;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IssueService {
    IssueResponseDto postIssue(Long userId, Long projectId, IssueRequestDto issueRequestDto);
    IssueResponseDto getIssueById(Long issueId);
    //List<IssueResponseDto> getIssueByProjectId(Long projectId);
    List<IssueResponseDto> getIssueByCondition(Long projectId, IssueSearchCondition condition);
    IssueResponseDto patchIssue(Long issueId, IssueUpdateDto issueUpdateDto);
    void deleteIssue(Long issueId);
    String uploadImage(Long issueId, MultipartFile file);
}
