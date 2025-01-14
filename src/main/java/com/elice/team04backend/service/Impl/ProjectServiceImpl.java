package com.elice.team04backend.service.Impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.repository.ProjectRepository;
import com.elice.team04backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

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
        return savedProject.from();
    }

    private String generatedProjectKey(ProjectRequestDto projectRequestDto) {
        StringBuilder sb = new StringBuilder();
        String[] s = projectRequestDto.getName().toUpperCase().split(" ");
        for (String value : s) {
            char input = value.charAt(0);
            if ('0' <= input && input <= '9' || input == ' ') {
                continue;
            }
            sb.append(input);
        }
        if (sb.toString().isEmpty() || sb.toString().isBlank()) {
            throw new IllegalStateException("임시예외");
        }
        return sb.toString();
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
