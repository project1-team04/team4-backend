package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.constant.Role;
import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.common.service.EmailService;
import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
import com.elice.team04backend.entity.*;
import com.elice.team04backend.repository.*;
import com.elice.team04backend.service.ProjectService;
import com.elice.team04backend.service.UserProjectRoleService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private final EmailService emailService;
    private Project savedProject;

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
        savedProject = projectRepository.save(project);

        UserProjectRole userProjectRole = UserProjectRole.builder()
                .user(User.builder().id(userId).build())
                .project(savedProject)
                .role(Role.MANAGER)
                .build();
        userProjectRoleRepository.save(userProjectRole);

        if(emails != null || !emails.isEmpty()) {
            for (String email : emails) {
                sendInvitationEmail(project.getName(), email);
            }
        }
        createDefaultLabels(savedProject);

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
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
        projectRepository.deleteById(projectId);
    }

        private void sendInvitationEmail(String projectName,String email) {
            String content = "안녕하세요,\n\n" +
                    "귀하를 " + projectName + " 프로젝트에 초대합니다.\n\n" +
                    "해당 페이지에 계정이 있으시다면 로그인 후 초대 내용을 확인하실 수 있으며, " +
                    "계정이 없으시다면 가입을 하신 후 프로젝트 매니저에게 다시 재요청을 부탁하셔야 합니다.\n\n" +
                    "감사합니다.\n\n" +
                    "초대 수락시 링크를 눌러주세요: http://localhost:8080/api/accept/invite?email=" + email;
            emailService.sendEmail(email, "안녕하세요! 귀하를 초대합니다", content);
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

        if (members.size() <= 1) {
            throw new CustomException(ErrorCode.LAST_MANAGER_CANNOT_LEAVE);
        }

        if (newManagerId == null) {
            throw new CustomException(ErrorCode.NEW_MANAGER_REQUIRED);
        }

        UserProjectRole newManager = members.stream()
                .filter(member -> member.getUser().getId().equals(newManagerId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NEW_MANAGER_NOT_FOUND));

        newManager.setRole(Role.MANAGER);
        userProjectRoleRepository.save(newManager);
    }

    @Override
    public String inviteMember(String email) {
        if (userRepository.existsByEmail(email)) {
            Optional<User> user = userRepository.findByEmail(email);
            UserProjectRole userProjectRole = UserProjectRole.builder()
                    .user(User.builder().id(user.get().getId()).build())
                    .project(savedProject)
                    .role(Role.MEMBER)
                    .build();
            userProjectRoleRepository.save(userProjectRole);
            return "성공적으로 프로젝트에 참여하였습니다";
        }
        return "해당 이메일은 가입이 되지 않은 이메일 입니다";
    }

}
