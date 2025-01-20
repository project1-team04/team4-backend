package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.constant.Role;
import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.common.service.EmailService;
import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
import com.elice.team04backend.entity.Label;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.entity.UserProjectRole;
import com.elice.team04backend.repository.LabelRepository;
import com.elice.team04backend.repository.ProjectRepository;
import com.elice.team04backend.repository.UserProjectRoleRepository;
import com.elice.team04backend.repository.UserRepository;
import com.elice.team04backend.service.ProjectService;
import com.elice.team04backend.service.UserProjectRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;
    private final UserProjectRoleRepository userProjectRoleRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    @Override
    public List<ProjectResponseDto> getProjectsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findByUserId(userId, pageable);
        return projectPage.stream()
                .map(Project::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectResponseDto getProjectById(Long projectId) {
        Project findedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        return findedProject.from();
    }

    @Override
    public ProjectResponseDto postProject(Long userId, ProjectRequestDto projectRequestDto, List<String> emails) {
        String projectKey = generatedProjectKey(projectRequestDto);
        Project project = projectRequestDto.from(projectKey);
        Project savedProject = projectRepository.save(project);

        if(emails != null || !emails.isEmpty()) {
            for (String email : emails) {
                if (userRepository.existsByEmail(email)) {
                    UserProjectRole userProjectRole = UserProjectRole.builder()
                            .user(User.builder().id(userId).build())
                            .project(savedProject)
                            .role(Role.MANAGER)
                            .build();
                    userProjectRoleRepository.save(userProjectRole);
                }
                String content = "안녕하세요,\n\n" +
                        "귀하를 " + project.getName() + " 프로젝트에 초대합니다.\n\n" +
                        "해당 페이지에 계정이 있으시다면 로그인 후 초대 내용을 확인하실 수 있으며, " +
                        "계정이 없으시다면 가입을 하신후 프로젝트 매니저에게 다시 재요청을 부탁하셔야 합니다.\n\n" +
                        "감사합니다.";
                emailService.sendEmail(email, "안녕하세요! 귀하를 초대합니다", content);
            }
        }
        createDefaultLabels(savedProject);

        return savedProject.from();
    }
    private void createDefaultLabels(Project project) {
        List<Label> defaultLabels = Arrays.asList(
                new Label(null, project, "None", "기본 라벨", "#808080", project.getIssues()),
                new Label(null, project, "Bug", "버그 라벨", "#FF0000", project.getIssues()),
                new Label(null, project, "Enhancement", "기능 개선 라벨", "#00FF00", project.getIssues())
        );
        labelRepository.saveAll(defaultLabels);
    }

    private String generatedProjectKey(ProjectRequestDto projectRequestDto) {
        StringBuilder sb = new StringBuilder();
        String[] words = projectRequestDto.getName().toUpperCase().split(" ");

        for (String word : words) {
            char firstChar = word.charAt(0);
            if (Character.isLetter(firstChar)) {
                sb.append(firstChar);
            }
        }

        if (sb.isEmpty()) {
            throw new CustomException(ErrorCode.KEY_CREATE_FAILED);
        }

        String baseKey = sb.toString();
        String projectKey = baseKey;
        int attempt = 1;

        while (projectRepository.existsByProjectKey(projectKey)) {
            char randomChar = (char) ('A' + (int) (Math.random() * 26));
            projectKey = baseKey + randomChar;
            if (attempt++ > 10) {
                throw new CustomException(ErrorCode.KEY_CREATE_FAILED);
            }
        }

        return projectKey;
    }

    @Override
    public ProjectResponseDto patchProject(Long userId, Long projectId, ProjectUpdateDto projectUpdateDto) {
        UserProjectRole userProjectRole = userProjectRoleRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_ACCESS_DENIED));

        if (userProjectRole.getRole() != Role.MANAGER) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        project.update(projectUpdateDto);
        Project updatedProject = projectRepository.save(project);
        return updatedProject.from();
    }

    @Override
    public void deleteProject(Long userId, Long projectId) {
        UserProjectRole userProjectRole = userProjectRoleRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_ACCESS_DENIED));

        if (userProjectRole.getRole() != Role.MANAGER) {
            throw new CustomException(ErrorCode.PROJECT_NOT_FOUND);
        }
        projectRepository.deleteById(projectId);
    }

}
