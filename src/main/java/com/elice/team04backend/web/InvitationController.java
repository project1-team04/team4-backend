package com.elice.team04backend.web;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.entity.EmailTemplate;
import com.elice.team04backend.entity.InvitationUrl;
import com.elice.team04backend.repository.EmailTemplateRepository;
import com.elice.team04backend.repository.InvitationUrlRepository;
import com.elice.team04backend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/accept")
@RequiredArgsConstructor
public class InvitationController {
    private final EmailTemplateRepository emailTemplateRepository;
    private final InvitationUrlRepository invitationUrlRepository;
    private final ProjectService projectService;

    @Operation(summary = "초대 수락", description = "초대 링크를 통해 프로젝트에 참여합니다.")
    @GetMapping("/{token}")
    public ResponseEntity<String> acceptInvitation(@PathVariable("token") String token) {
        projectService.acceptInvitation(token);

        EmailTemplate htmlResponse = emailTemplateRepository.findByTemplateKey("INVITATION_ACCEPTED")
                .orElseThrow(() -> new CustomException(ErrorCode.TEMPLATE_NOT_FOUND));

        String loginUrl = invitationUrlRepository.findByTemplateKey("LOGIN_URL")
                .map(InvitationUrl::getUrlFormat)
                .orElseThrow(() -> new CustomException(ErrorCode.TEMPLATE_NOT_FOUND));

        String content = htmlResponse.getContent().replace("{loginUrl}", loginUrl);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(content);
    }
}
