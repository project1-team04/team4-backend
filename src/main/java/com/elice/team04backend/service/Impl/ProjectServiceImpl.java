package com.elice.team04backend.service.Impl;

import com.elice.team04backend.dto.Project.ProjectRequestDto;
import com.elice.team04backend.dto.Project.ProjectResponseDto;
import com.elice.team04backend.dto.Project.ProjectUpdateDto;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.repository.ProjectRepository;
import com.elice.team04backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    //@Preauthorize 사용하면 role이 MEMBER인 유저만 사용하게 가능 예외처리 안해도됨
    @Override
    public ProjectResponseDto postProject(ProjectRequestDto projectRequestDto) {
        Project project = projectRequestDto.from();
        Project createProject = projectRepository.save(project);
        return createProject.from();
    }

    @Override
    public void patchProject(ProjectUpdateDto projectUpdateDto) {
        Project project = projectRepository.findById(projectUpdateDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("임시예외"));
        project.update(projectUpdateDto);
        Project updateProject = projectRepository.save(project);
    }
}
