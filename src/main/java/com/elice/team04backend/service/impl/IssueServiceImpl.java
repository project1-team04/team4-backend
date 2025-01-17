package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.dto.issue.IssueRequestDto;
import com.elice.team04backend.dto.issue.IssueResponseDto;
import com.elice.team04backend.dto.issue.IssueUpdateDto;
import com.elice.team04backend.entity.Issue;
import com.elice.team04backend.entity.IssueImage;
import com.elice.team04backend.entity.Label;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.repository.IssueRepository;
import com.elice.team04backend.repository.LabelRepository;
import com.elice.team04backend.repository.ProjectRepository;
import com.elice.team04backend.repository.UserRepository;
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

    @Override
    public IssueResponseDto postIssue(Long projectId, IssueRequestDto issueRequestDto, List<MultipartFile> files) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        Label label;
        label = settingLabel(projectId, issueRequestDto);

        String issueKey = generateIssueKey(project);
        Issue issue = issueRequestDto.from(project, label, issueKey);

        uploadImage(files, issue);

        project.addIssue(issue);
        Issue savedIssue = issueRepository.save(issue);
        return savedIssue.from();
    }

    private Label settingLabel(Long projectId, IssueRequestDto issueRequestDto) {
        Label label;
        if (issueRequestDto.getLabelId() != null) {
            label = labelRepository.findById(issueRequestDto.getLabelId())
                    .orElseThrow(() -> new CustomException(ErrorCode.LABEL_NOT_FOUND));
        } else {
            label = labelRepository.findByProjectIdAndName(projectId, "None")
                    .orElseThrow(() -> new CustomException(ErrorCode.LABEL_NOT_FOUND));
        }
        return label;
    }

    private void uploadImage(List<MultipartFile> files, Issue issue) {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    String imageUrl = firebaseStorageService.uploadImage(file);
                    String originalName = file.getOriginalFilename();
                    IssueImage issueImage = IssueImage.builder()
                            .issue(issue)
                            .imageUrl(imageUrl)
                            .originalName(originalName)
                            .build();
                    issue.addIssueImages(issueImage);
                } catch (IOException e) {
                    throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
                }
            }
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
}
