package com.elice.team04backend.web;

import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.dto.issue.IssueRequestDto;
import com.elice.team04backend.dto.issue.IssueResponseDto;
import com.elice.team04backend.dto.issue.IssueUpdateDto;
import com.elice.team04backend.dto.search.IssueSearchCondition;
import com.elice.team04backend.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/issues")
@RequiredArgsConstructor
@Tag(name = "Issue 관리", description = "Issue 관련 API 입니다.")
public class IssueController {

    private final IssueService issueService;

    @Operation(summary = "이슈 생성", description = "이슈를 생성하는 기능입니다.")
    @PostMapping
    public ResponseEntity<IssueResponseDto> postIssue(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("projectId") Long projectId,
            @Valid @RequestBody IssueRequestDto issueRequestDto) {
        IssueResponseDto issueResponseDto = issueService.postIssue(userDetails.getUserId(), projectId, issueRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(issueResponseDto);
    }

    @Operation(summary = "이슈 조건 검색", description = "프로젝트에 속해있는 이슈를 조건 검색하는 기능입니다.")
    @GetMapping
    public ResponseEntity<List<IssueResponseDto>> getIssuesByCondition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("projectId") Long projectId,
            @RequestParam(name = "condition", required = false) String condition) {
        IssueSearchCondition searchCondition = new IssueSearchCondition(condition);
        List<IssueResponseDto> issueResponseDtos = issueService.getIssueByCondition(projectId, searchCondition);
        return ResponseEntity.ok(issueResponseDtos);
    }

    @Operation(summary = "단일 이슈 조회", description = "단일 이슈를 조회하는 기능입니다.")
    @GetMapping("/{issueId}")
    public ResponseEntity<IssueResponseDto> getIssueById(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("projectId") Long projectId,
            @PathVariable("issueId") Long issueId) {
        IssueResponseDto issueResponseDto = issueService.getIssueById(issueId);
        return ResponseEntity.ok(issueResponseDto);
    }

    @Operation(summary = "단일 이슈 수정", description = "단일 이슈를 수정하는 기능입니다.")
    @PatchMapping("/{issueId}")
    public ResponseEntity<IssueResponseDto> patchIssue(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("projectId") Long projectId,
            @PathVariable("issueId") Long issueId,
            @Valid @RequestBody IssueUpdateDto issueUpdateDto) {
        IssueResponseDto issueResponseDto = issueService.patchIssue(issueId, issueUpdateDto);
        return ResponseEntity.ok(issueResponseDto);
    }

    @Operation(summary = "단일 이슈 삭제", description = "단일 이슈를 삭제하는 기능입니다.")
    @DeleteMapping("/{issueId}")
    public ResponseEntity<Void> deleteIssue(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("projectId") Long projectId,
            @PathVariable("issueId") Long issueId) {
        issueService.deleteIssue(issueId);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "이미지 업로드", description = "이슈에 단일 이미지를 업로드하는 기능입니다.")
    @PostMapping("/{issueId}/image")
    public ResponseEntity<String> uploadImage(
            @PathVariable("projectId") Long projectId,
            @PathVariable("issueId") Long issueId,
            @RequestPart("file") MultipartFile file) throws IOException {
        String imageUrl = issueService.uploadImage(issueId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageUrl);
    }
}

