package com.elice.team04backend.web;

import com.elice.team04backend.dto.label.LabelRequestDto;
import com.elice.team04backend.dto.label.LabelResponseDto;
import com.elice.team04backend.dto.label.LabelUpdateDto;
import com.elice.team04backend.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @PostMapping
    public ResponseEntity<LabelResponseDto> postLabel(
            @Valid @RequestBody LabelRequestDto labelRequestDto) {
        LabelResponseDto labelResponseDto = labelService.postLabel(labelRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(labelResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseDto>> getAllLabels() {
        List<LabelResponseDto> labelResponseDtos = labelService.getAllLabels();
        return ResponseEntity.ok(labelResponseDtos);
    }

    @PatchMapping("/{labelId}")
    public ResponseEntity<LabelResponseDto> patchLabel(
            @PathVariable Long labelId,
            @Valid @RequestBody LabelUpdateDto labelUpdateDto) {
        LabelResponseDto labelResponseDto = labelService.patchLabel(labelId, labelUpdateDto);
        return ResponseEntity.ok(labelResponseDto);
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long labelId) {
        labelService.deleteLabel(labelId);
        return ResponseEntity.noContent().build();
    }
}
