package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.dto.label.LabelRequestDto;
import com.elice.team04backend.dto.label.LabelResponseDto;
import com.elice.team04backend.dto.label.LabelUpdateDto;
import com.elice.team04backend.entity.Label;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.repository.LabelRepository;
import com.elice.team04backend.repository.ProjectRepository;
import com.elice.team04backend.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final ProjectRepository projectRepository;

    @Override
    public LabelResponseDto postLabel(Long projectId, LabelRequestDto labelRequestDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        Label label = labelRequestDto.from(project);
        project.addLabel(label);
        Label savedLabel = labelRepository.save(label);
        return savedLabel.from();
    }

    @Transactional(readOnly = true)
    @Override
    public List<LabelResponseDto> getAllLabelsByProjectId(Long projectId) {
        return labelRepository.findByProjectId(projectId)
                .stream()
                .map(Label::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public LabelResponseDto getLabelById(Long labelId) {
        Label findedLabel = labelRepository.findById(labelId)
                .orElseThrow(() -> new CustomException(ErrorCode.LABEL_NOT_FOUND));
        return findedLabel.from();
    }

    @Override
    public LabelResponseDto patchLabel(Long labelId, LabelUpdateDto labelUpdateDto) {
        Label findedLabel = labelRepository.findById(labelId)
                .orElseThrow(() -> new CustomException(ErrorCode.LABEL_NOT_FOUND));
        findedLabel.update(labelUpdateDto);
        Label updatedLabel = labelRepository.save(findedLabel);
        return updatedLabel.from();
    }

    @Override
    public void deleteLabel(Long labelId) {
        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new CustomException(ErrorCode.LABEL_NOT_FOUND));
        labelRepository.delete(label);
    }
}
