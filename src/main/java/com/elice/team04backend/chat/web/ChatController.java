package com.elice.team04backend.chat.web;

import com.elice.team04backend.chat.entity.Message;
import com.elice.team04backend.chat.repository.ChatMessageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "채팅 관리", description = "채팅 관련 API 입니다.")
public class ChatController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Operation(summary = "채팅방 기록 불러오기", description = "디비에있는 채팅기록들을 issue_id로 가지고 옵니다")
    @GetMapping("/{issueId}")
    public Iterable<Message> selectMessages(@Parameter(description = "채팅 기록을 불러올 issue ID") @PathVariable String issueId) {
        return chatMessageRepository.findByIssueId(issueId);
    }
}
