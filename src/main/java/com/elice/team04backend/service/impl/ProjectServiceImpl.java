package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.constant.Role;
import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
import com.elice.team04backend.entity.*;
import com.elice.team04backend.repository.*;
import com.elice.team04backend.service.ProjectService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;
    private final UserProjectRoleRepository userProjectRoleRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final JavaMailSender mailSender;
    private final IssueRepository issueRepository;

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
    public ProjectResponseDto postProject(Long userId, ProjectRequestDto projectRequestDto) {
        String projectKey = generatedProjectKey(projectRequestDto);
        Project project = projectRequestDto.from(projectKey);

        Project savedProject = projectRepository.save(project);
        createDefaultLabels(savedProject);

        UserProjectRole userProjectRole = setUserAsManager(userId, savedProject);
        userProjectRoleRepository.save(userProjectRole);

        return savedProject.from();
    }

    private static UserProjectRole setUserAsManager(Long userId, Project savedProject) {
        UserProjectRole userProjectRole = UserProjectRole.builder()
                        .user(User.builder().id(userId).build())
                        .project(savedProject)
                        .role(Role.MANAGER)
                        .build();
        return userProjectRole;
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
            throw new CustomException(ErrorCode.PROJECT_KEY_CREATE_FAILED);
        }

        String baseKey = sb.toString();
        String projectKey = baseKey;
        int attempt = 1;

        while (projectRepository.existsByProjectKey(projectKey)) {
            char randomChar = (char) ('A' + (int) (Math.random() * 26));
            projectKey = baseKey + randomChar;
            if (attempt++ > 10) {
                throw new CustomException(ErrorCode.PROJECT_CREATE_FAILED);
            }
        }

        return projectKey;
    }

    @Override
    public ProjectResponseDto patchProject(Long userId, Long projectId, ProjectUpdateDto projectUpdateDto) {

        UserProjectRole userProjectRole = userProjectRoleRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_ACCESS_DENIED));

        if (userProjectRole.getRole() != Role.MANAGER) {
            throw new CustomException(ErrorCode.ROLE_PERMISSION_DENIED);
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        String oldProjectKey = project.getProjectKey();
        String newProjectKey = updateProjectKey(projectUpdateDto);

        project.update(projectUpdateDto);

        project.updateProjectKey(newProjectKey);
        updateIssueKeys(project, oldProjectKey, newProjectKey);

        Project updatedProject = projectRepository.save(project);
        return updatedProject.from();
    }

    private String updateProjectKey(ProjectUpdateDto projectUpdateDto) {
        StringBuilder sb = new StringBuilder();
        String[] words = projectUpdateDto.getName().toUpperCase().split(" ");

        for (String word : words) {
            char firstChar = word.charAt(0);
            if (Character.isLetter(firstChar)) {
                sb.append(firstChar);
            }
        }

        if (sb.isEmpty()) {
            throw new CustomException(ErrorCode.PROJECT_KEY_CREATE_FAILED);
        }

        String baseKey = sb.toString();
        String projectKey = baseKey;
        int attempt = 1;

        while (projectRepository.existsByProjectKey(projectKey)) {
            char randomChar = (char) ('A' + (int) (Math.random() * 26));
            projectKey = baseKey + randomChar;
            if (attempt++ > 10) {
                throw new CustomException(ErrorCode.PROJECT_CREATE_FAILED);
            }
        }

        return projectKey;
    }

    private void updateIssueKeys(Project project, String oldProjectKey, String newProjectKey) {
        List<Issue> issues = issueRepository.findByProjectId(project.getId());

        for (Issue issue : issues) {
            String updatedIssueKey = issue.getIssueKey().replace(oldProjectKey, newProjectKey);
            issue.updateIssueKey(updatedIssueKey);
            issueRepository.save(issue);
        }
    }

    @Override
    public void deleteProject(Long userId, Long projectId) {
        UserProjectRole userProjectRole = userProjectRoleRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_ACCESS_DENIED));

        if (userProjectRole.getRole() != Role.MANAGER) {
            throw new CustomException(ErrorCode.ROLE_PERMISSION_DENIED);
        }
        projectRepository.deleteById(projectId);
    }

    @Override
    public void inviteUserToProject(Long managerId, Long projectId, String email) {
        UserProjectRole managerRole = userProjectRoleRepository.findByUserIdAndProjectId(managerId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_ACCESS_DENIED));

        if (managerRole.getRole() != Role.MANAGER) {
            throw new CustomException(ErrorCode.ROLE_PERMISSION_DENIED);
        }

        User invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String token = UUID.randomUUID().toString();
        Invitation invitation = Invitation.builder()
                .user(invitedUser)
                .project(managerRole.getProject())
                .token(token)
                .build();
        invitationRepository.save(invitation);

        sendInvitationEmail(email, token);
    }



    private void sendInvitationEmail(String email, String token) {
        String invitationLink = String.format("http://localhost:8080/api/accept/%s", token);
        String subject = "Project Invitation";
        String content = String.format(
                "<p>You have been invited to join a project.</p>" +
                        "<p>Click the link below to accept the invitation:</p>" +
                        "<a href=\"%s\">Accept Invitation</a>", invitationLink);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Override
    public void acceptInvitation(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITATION));

        UserProjectRole newMember = setUserAsMember(invitation);
        userProjectRoleRepository.save(newMember);

        invitationRepository.deleteById(invitation.getId());
    }

    private static UserProjectRole setUserAsMember(Invitation invitation) {
        UserProjectRole newMember = UserProjectRole.builder()
                .user(invitation.getUser())
                .project(invitation.getProject())
                .role(Role.MEMBER)
                .build();
        return newMember;
    }

    @Override
    public void leaveProject(Long userId, Long projectId, Long newManagerId) {
        UserProjectRole userRole = userProjectRoleRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_ACCESS_DENIED));

        if (userRole.getRole() == Role.MANAGER) {
            handleManagerLeaving(projectId, userId, newManagerId);
        }

        userProjectRoleRepository.delete(userRole);
    }

    private void handleManagerLeaving(Long userId, Long projectId, Long newManagerId) {
        List<UserProjectRole> members = userProjectRoleRepository.findAllByProjectId(projectId);

        if (newManagerId == null) {
            throw new CustomException(ErrorCode.NEW_MANAGER_REQUIRED);
        }

        if (members.size() <= 1) {
            throw new CustomException(ErrorCode.LAST_MANAGER_CANNOT_LEAVE);
        }

        UserProjectRole newManager = members.stream()
                .filter(member -> member.getUser().getId().equals(newManagerId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NEW_MANAGER_NOT_FOUND));

        newManager.setRole(Role.MANAGER);
        userProjectRoleRepository.save(newManager);
    }

    @Override
    public void assignManager(Long currentManagerId, Long projectId, Long newManagerId) {
        UserProjectRole currentManagerRole = userProjectRoleRepository.findByUserIdAndProjectId(currentManagerId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_ACCESS_DENIED));

        if (currentManagerRole.getRole() != Role.MANAGER) {
            throw new CustomException(ErrorCode.ROLE_PERMISSION_DENIED);
        }

        UserProjectRole newManagerRole = userProjectRoleRepository.findByUserIdAndProjectId(newManagerId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEW_MANAGER_NOT_FOUND));

        newManagerRole.setRole(Role.MANAGER);
        currentManagerRole.setRole(Role.MEMBER);

        userProjectRoleRepository.save(currentManagerRole);
        userProjectRoleRepository.save(newManagerRole);
    }

}
