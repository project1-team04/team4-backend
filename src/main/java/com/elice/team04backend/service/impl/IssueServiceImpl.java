package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.dto.issue.IssueRequestDto;
import com.elice.team04backend.dto.issue.IssueResponseDto;
import com.elice.team04backend.dto.issue.IssueUpdateDto;
import com.elice.team04backend.dto.search.IssueSearchCondition;
import com.elice.team04backend.entity.*;
import com.elice.team04backend.repository.*;
import com.elice.team04backend.service.FirebaseStorageService;
import com.elice.team04backend.service.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final UserRepository userRepository;
    private final UserProjectRoleRepository userProjectRoleRepository;

    @Override
    public IssueResponseDto postIssue(Long userId, Long projectId, IssueRequestDto issueRequestDto) {
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean isAssigneeInProject = userProjectRoleRepository.existsByUserIdAndProjectId(
                issueRequestDto.getAssigneeUserId(), projectId);
        if (!isAssigneeInProject) {
            throw new CustomException(ErrorCode.USER_NOT_IN_PROJECT);
        }

        User assignee = userRepository.findById(issueRequestDto.getAssigneeUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        Label label = settingLabel(projectId, issueRequestDto);

        String issueKey = generateIssueKey(project);
        Issue issue = issueRequestDto.from(reporter, assignee, project, label, issueKey);

        reporter.addReporterIssue(issue);
        assignee.addAssigneeIssue(issue);
        project.addIssue(issue);

        Issue savedIssue = issueRepository.save(issue);
        return savedIssue.from();
    }

    private Label settingLabel(Long projectId, IssueRequestDto issueRequestDto) {
        if (issueRequestDto.getLabelId() != null) {
            return labelRepository.findById(issueRequestDto.getLabelId())
                    .orElseThrow(() -> new CustomException(ErrorCode.LABEL_NOT_FOUND));
        } else {
            return labelRepository.findByProjectIdAndName(projectId, "None")
                    .orElseThrow(() -> new CustomException(ErrorCode.LABEL_NOT_FOUND));
        }
    }

    private String generateIssueKey(Project project) {
        String projectKey = project.getProjectKey();
        Long projectId = project.getId();

        int maxIssueIndex = issueRepository.findMaxIssueIndexByProject(projectId, projectKey);
        int nextIssueIndex = maxIssueIndex + 1;
        return projectKey + "_" + nextIssueIndex;
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponseDto getIssueById(Long issueId) {
        Issue findedIssue = issueRepository.findById(issueId)
                .orElseThrow(() -> new CustomException(ErrorCode.ISSUE_NOT_FOUND));
        return findedIssue.from();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseDto> getIssueByProjectId(Long projectId) {
        return issueRepository.findByProjectId(projectId)
                .stream()
                .map(Issue::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseDto> getIssueByCondition(Long projectId, IssueSearchCondition condition) {
        return issueRepository.searchIssues(projectId, condition)
                .stream()
                .map(Issue::from)
                .collect(Collectors.toList());
    }


    @Override
    public IssueResponseDto patchIssue(Long issueId, IssueUpdateDto issueUpdateDto) {
        Issue findedIssue = issueRepository.findById(issueId)
                .orElseThrow(() -> new CustomException(ErrorCode.ISSUE_NOT_FOUND));
        findedIssue.update(issueUpdateDto);
        return findedIssue.from();
    }

    @Override
    public void deleteIssue(Long issueId) {
        Issue findedIssue = issueRepository.findById(issueId)
                .orElseThrow(() -> new CustomException(ErrorCode.ISSUE_NOT_FOUND));
        Project project = findedIssue.getProject();
        project.getIssues().remove(findedIssue);
        issueRepository.delete(findedIssue);
    }

    @Override
    public String uploadImage(Long issueId, MultipartFile file) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new CustomException(ErrorCode.ISSUE_NOT_FOUND));

        try {
            String imageUrl = firebaseStorageService.uploadImage(file);
            String originalName = file.getOriginalFilename();

            IssueImage issueImage = IssueImage.builder()
                    .issue(issue)
                    .imageUrl(imageUrl)
                    .originalName(originalName)
                    .build();

            issue.addIssueImages(issueImage);
            issueRepository.save(issue);

            return imageUrl;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}
