package com.elice.team04backend.service.Impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
import com.elice.team04backend.entity.Label;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.repository.LabelRepository;
import com.elice.team04backend.repository.ProjectRepository;
import com.elice.team04backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
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

    /*
    @Preauthorize 사용하면 role이 MEMBER인 유저만 사용하게 가능 예외처리 안해도됨
    CustomException 적용해야해서 임시 예외 설정
     */
    @Transactional(readOnly = true)
    @Override
    public ProjectResponseDto getProjectById(Long projectId) {
        Project findedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        return findedProject.from();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(Project::from)
                .toList();
    }

    @Override
    public ProjectResponseDto postProject(ProjectRequestDto projectRequestDto) {
        String projectKey = generatedProjectKey(projectRequestDto);
        Project project = projectRequestDto.from(projectKey);
        Project savedProject = projectRepository.save(project);

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
    public ProjectResponseDto patchProject(Long projectId, ProjectUpdateDto projectUpdateDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        project.update(projectUpdateDto);
        Project updatedProject = projectRepository.save(project);
        return updatedProject.from();
    }

    @Override
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        projectRepository.delete(project);
    }
}
