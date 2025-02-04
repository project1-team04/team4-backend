package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.config.CacheConfig;
import com.elice.team04backend.common.constant.Role;
import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.common.service.EmailService;
import com.elice.team04backend.dto.project.*;
import com.elice.team04backend.dto.search.ProjectSearchCondition;
import com.elice.team04backend.entity.*;
import com.elice.team04backend.repository.*;
import com.elice.team04backend.service.CacheService;
import com.elice.team04backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;
    private final UserProjectRoleRepository userProjectRoleRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final IssueRepository issueRepository;
    private final CacheConfig cacheConfig;
    private final CacheService cacheService;
    private final EmailService emailService;

    @Qualifier("task")
    private final Executor task;

    private static final int MAX_PROJECT_KEY_GENERATE_ATTEMPTS = 100;

    @Cacheable(value = "userProjects", key = "#userId + '_' + #page + '_' + #size")
    @Override
    public ProjectTotalResponseDto getProjectsByUser(Long userId, int page, int size) {
        log.info("userId: {}, page: {}, size: {}", userId, page, size);
        return getProjectsByUserInternal(userId, page, size);
    }
    @Transactional(readOnly = true)
    @Override
    public ProjectTotalResponseDto getProjectsByUserInternal(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Project> projectPage = projectRepository.findByUserId(userId, pageable);

        List<ProjectResponseDto> projectResponseDtos = projectPage.stream()
                .map(Project::from)
                .toList();

        return new ProjectTotalResponseDto(projectResponseDtos, projectPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProjectSearchResponseDto getProjectByCondition(Long userId, ProjectSearchCondition condition, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        CompletableFuture<List<Project>> asyncProjects = CompletableFuture.supplyAsync(() -> {
            log.info("condition asyncProjects: {}", Thread.currentThread().getName());
            return projectRepository.fetchProjects(userId, condition, pageable);
        }, task);

        CompletableFuture<Long> asyncProjectsCnt = CompletableFuture.supplyAsync(() -> {
            log.info("condition asyncProjectsCnt: {}", Thread.currentThread().getName());
            return projectRepository.fetchProjectCount(userId, condition);
        }, task);

        CompletableFuture.allOf(asyncProjects, asyncProjectsCnt).join();

        try {
            List<Project> projects = asyncProjects.get();
            Long total = asyncProjectsCnt.get(); // 전체 프로젝트 수

            List<ProjectResponseDto> projectResponseDtos = projects.stream()
                    .map(Project::from)
                    .toList();
            return new ProjectSearchResponseDto(projectResponseDtos, projects.size(), total); // projects.size()는 페이지 내 항목 수

        } catch (ExecutionException e) {
            throw new CustomException(ErrorCode.ASYNC_EXECUTION_FAILED);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.ASYNC_INTERRUPTED);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectResponseDto getProjectById(Long projectId) {
        Project findedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        return findedProject.from();
    }

    @CacheEvict(value = "userProjects", allEntries = true)
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
        return UserProjectRole.builder()
                .user(User.builder().id(userId).build())
                .project(savedProject)
                .role(Role.MANAGER)
                .build();
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
        StringBuilder baseKeyBuilder = new StringBuilder();
        String[] words = projectRequestDto.getName().trim().toUpperCase().split(" ");

        for (String word : words) {
            char firstChar = word.charAt(0);
            if (Character.isLetter(firstChar)) {
                baseKeyBuilder.append(firstChar);
            }
        }

        if (baseKeyBuilder.isEmpty()) {
            throw new CustomException(ErrorCode.PROJECT_KEY_CREATE_FAILED);
        }

        String baseKey = baseKeyBuilder.toString();
        List<String> existingKeys = projectRepository.findAllProjectKeysByPrefix(baseKey);
        String projectKey = baseKey;
        int attempt = 0;

        while (existingKeys.contains(projectKey)) {
            projectKey = baseKey + generateSuffix(attempt++);
            if (attempt > MAX_PROJECT_KEY_GENERATE_ATTEMPTS) {
                throw new CustomException(ErrorCode.PROJECT_CREATE_FAILED);
            }
        }
        return projectKey;
    }

    private String generateSuffix(int attempt) {
        StringBuilder suffixBuilder = new StringBuilder();
        do {
            suffixBuilder.append((char) ('A' + (attempt % 26))); // A ~ Z까지 반복
            attempt /= 26; // 자리수 증가
        } while (attempt > 0);

        return suffixBuilder.reverse().toString(); // 예: A ~ Z 다음  AA, AB, ... 순으로 프로젝트 키 생성
    }

    @Override
    public ProjectResponseDto patchProject(Long userId, Long projectId, ProjectUpdateDto projectUpdateDto) {
        int pageSize = cacheConfig.getPageSize();
        int currentPage = projectRepository.getProjectPageNumber(userId, projectId, pageSize);
        String cacheKey = userId + "_" + currentPage + "_" + pageSize;

        log.info("Cache Key: userProjects::{}", cacheKey);

        cacheService.evictCache("userProjects", cacheKey); //getProjectsByUser 와 리턴타입 달라서 삭제가 안되는 문제 발생 따라서 캐시를 명시적으로 삭제처리
        return patchProjectInternal(userId, projectId, currentPage, pageSize, projectUpdateDto);
    }

    @Override
    public ProjectResponseDto patchProjectInternal(Long userId, Long projectId, int page, int size, ProjectUpdateDto projectUpdateDto) {

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
        if (project.getIssueCount() != 0) {
            updateIssueKeys(project, oldProjectKey, newProjectKey);
        }

        Project updatedProject = projectRepository.save(project);

        return updatedProject.from();
    }

    private String updateProjectKey(ProjectUpdateDto projectUpdateDto) {
        StringBuilder baseKeyBuilder = new StringBuilder();
        String[] words = projectUpdateDto.getName().trim().toUpperCase().split(" ");

        for (String word : words) {
            char firstChar = word.charAt(0);
            if (Character.isLetter(firstChar)) {
                baseKeyBuilder.append(firstChar);
            }
        }

        if (baseKeyBuilder.isEmpty()) {
            throw new CustomException(ErrorCode.PROJECT_KEY_CREATE_FAILED);
        }

        String baseKey = baseKeyBuilder.toString();
        List<String> existingKeys = projectRepository.findAllProjectKeysByPrefix(baseKey);
        String projectKey = baseKey;
        int attempt = 0;

        while (existingKeys.contains(projectKey)) {
            projectKey = baseKey + generateSuffix(attempt++);
            if (attempt > MAX_PROJECT_KEY_GENERATE_ATTEMPTS) {
                throw new CustomException(ErrorCode.PROJECT_CREATE_FAILED);
            }
        }
        return projectKey;
    }

    private void updateIssueKeys(Project project, String oldProjectKey, String newProjectKey) {
        issueRepository.bulkUpdateIssueKeys(project.getId(), oldProjectKey, newProjectKey);
    }

    @CacheEvict(value = "userProjects", allEntries = true)
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
    public ProjectUserInfoDto inviteSingleUsers(Long projectId, String email) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (userProjectRoleRepository.existsByUserIdAndProjectId(user.getId(), projectId)) {
            throw new CustomException(ErrorCode.USER_ALREADY_IN_PROJECT);
        }

        if (invitationRepository.existsByUserIdAndProjectId(user.getId(), projectId)) {
            throw new CustomException(ErrorCode.USER_ALREADY_INVITED);
        }

        ProjectUserInfoDto projectUserInfoDto = new ProjectUserInfoDto();
        projectUserInfoDto.setEmail(user.getEmail());
        projectUserInfoDto.setName(user.getUsername());

        String token = UUID.randomUUID().toString();
        Invitation invitation = Invitation.builder()
                .user(user)
                .project(project)
                .token(token)
                .build();
        invitationRepository.save(invitation);

        sendInvitationEmail(project.getName(), email, token);
        return projectUserInfoDto;
    }

    //프론트 확인을 위해서 url 수정, 백에서 테스트 원할 시 코드 수정하고 진행 해야함
    //back String invitationLink = String.format("http://localhost:8080/api/accept/%s", token);
    //front String invitationLink = String.format("http://localhost:3000/accept/%s", token);
    //http://34.22.102.28:8080
    private void sendInvitationEmail(String projectName, String email, String token) {
        String invitationLink = String.format("http://34.22.102.28:8080/api/accept/%s", token);
        Map<String, String> variables = Map.of(
                "projectName", projectName,
                "invitationLink", invitationLink
        );

        emailService.sendEmailWithTemplate("INVITATION_EMAIL", email, variables);
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
        return UserProjectRole.builder()
                .user(invitation.getUser())
                .project(invitation.getProject())
                .role(Role.MEMBER)
                .build();
    }

    @Override
    public void leaveProject(Long userId, Long projectId, Long newManagerId) {
        UserProjectRole userRole = userProjectRoleRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_ACCESS_DENIED));

        if (userRole.getRole() == Role.MANAGER) {
            handleManagerLeaving(projectId, newManagerId);
        }

        userProjectRoleRepository.delete(userRole);
    }

    private void handleManagerLeaving(Long projectId, Long newManagerId) {
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
