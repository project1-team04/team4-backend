package com.elice.team04backend.web;

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

@RestController
@Slf4j
@RequestMapping("/api/accept")
@RequiredArgsConstructor
public class InvitationController {
    private final ProjectService projectService;

    @Operation(summary = "초대 수락", description = "초대 링크를 통해 프로젝트에 참여합니다.")
    @GetMapping("/{token}")
    public ResponseEntity<String> acceptInvitation(@PathVariable String token) {
        projectService.acceptInvitation(token);

        String htmlResponse = """
        <html>
            <head>
                <title>Invitation Accepted</title>
            </head>
            <body>
                <h1>Invitation Accepted</h1>
                <p>You have successfully joined the project!</p>
            </body>
        </html>
        """;

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlResponse);
    }
}
