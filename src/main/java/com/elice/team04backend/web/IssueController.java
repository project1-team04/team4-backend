package com.elice.team04backend.web;

import com.elice.team04backend.dto.issue.IssueRequestDto;
import com.elice.team04backend.dto.issue.IssueResponseDto;
import com.elice.team04backend.dto.issue.IssueUpdateDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
import com.elice.team04backend.service.FirebaseStorageService;
import com.elice.team04backend.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final FirebaseStorageService firebaseStorageService;

    @PostMapping
    public ResponseEntity<IssueResponseDto> postIssue(
            @PathVariable Long projectId,
            @Valid @RequestPart(value = "issue") IssueRequestDto issueRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        IssueResponseDto issueResponseDto = issueService.postIssue(projectId, issueRequestDto, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(issueResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseDto>> getIssuesByProject(@PathVariable Long projectId) {
        List<IssueResponseDto> issueResponseDtos = issueService.getIssueByProjectId(projectId);
        return ResponseEntity.ok(issueResponseDtos);
    }

    @PatchMapping("/{issueId}")
    public ResponseEntity<IssueResponseDto> patchIssue(
            @PathVariable Long issueId,
            @Valid @RequestBody IssueUpdateDto issueUpdateDto) {
        IssueResponseDto issueResponseDto = issueService.patchIssue(issueId, issueUpdateDto);
        return ResponseEntity.ok(issueResponseDto);
    }

    @DeleteMapping("/{issueId}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long issueId) {
        issueService.deleteIssue(issueId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{issueId}/images")
    public ResponseEntity<String> uploadIssueImage(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @RequestParam("file") MultipartFile file) throws IOException {

        String imageUrl = firebaseStorageService.uploadImage(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(imageUrl);
    }
}

