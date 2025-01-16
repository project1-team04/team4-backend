package com.elice.team04backend.service.impl;

import com.elice.team04backend.dto.Project.ProjectRequestDto;
import com.elice.team04backend.dto.Project.ProjectResponseDto;
import com.elice.team04backend.dto.Project.ProjectUpdateDto;
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
    @Override
    public ProjectResponseDto getProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID: " + projectId));
        return project.from();
    }

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
            if ('0' <= input && input <= '9') {
                continue;
            }
            sb.append(input);
        }
        return sb.toString();
    }

    @Override
    public ProjectResponseDto patchProject(ProjectUpdateDto projectUpdateDto) {
        Project project = projectRepository.findById(projectUpdateDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID: " + projectUpdateDto.getId()));
        project.updateName(projectUpdateDto);
        Project updateProject = projectRepository.save(project);
        return updateProject.from();
    }

    @Override
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID: " + projectId));
        projectRepository.delete(project);
    }
}
