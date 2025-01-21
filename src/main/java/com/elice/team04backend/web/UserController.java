package com.elice.team04backend.web;

import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.dto.user.GetProfileResponseDto;
import com.elice.team04backend.dto.user.PatchProfileRequestDto;
import com.elice.team04backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원의 프로필 관리 기능입니다.")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "이메일, 사용자명, 프로필이미지를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<GetProfileResponseDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(userService.getProfile(userDetails.getUserId()));
    }

    @Operation(summary = "프로필 변경", description = "프로필 변경입니다. 해당 API를 사용하려면, https://kdt-gitlab.elice.io/pttrack/class_01/web_project_i/team04/team04-documentations/-/issues/27 를 봐주세요")
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> patchProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestPart(value = "image", required = false) MultipartFile profileImage,
                                          @RequestPart(value = "text") @Valid PatchProfileRequestDto patchProfileRequestDto
                                          ) {

        userService.patchProfile(userDetails.getUserId(), patchProfileRequestDto, profileImage);

        return ResponseEntity.ok().build();
    }

}
