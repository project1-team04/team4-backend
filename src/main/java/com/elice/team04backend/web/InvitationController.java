package com.elice.team04backend.web;

import com.elice.team04backend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/accept")
@RequiredArgsConstructor
public class InvitationController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 참여에 수락시에 진행되는 api 입니다. 이메일을 넣어주면 해당 이메일이 프로젝트에 추가됩니다", security = {} )
    @GetMapping("/invite")
    public ResponseEntity inviteMember(@RequestParam String email) {

        String text = projectService.inviteMember(email);

        return ResponseEntity.ok(text);
    }
}
