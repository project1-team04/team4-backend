package com.elice.team04backend.service;

import com.elice.team04backend.dto.label.LabelRequestDto;
import com.elice.team04backend.dto.label.LabelResponseDto;
import com.elice.team04backend.dto.label.LabelUpdateDto;

import java.util.List;

public interface LabelService {
    LabelResponseDto postLabel(LabelRequestDto labelRequestDto);
    List<LabelResponseDto> getAllLabels();

    LabelResponseDto getLabelById(Long labelId);
    LabelResponseDto patchLabel(Long labelId, LabelUpdateDto labelUpdateDto);
    void deleteLabel(Long labelId);
}
