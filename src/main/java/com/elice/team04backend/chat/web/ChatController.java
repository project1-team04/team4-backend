package com.elice.team04backend.chat.web;

import com.elice.team04backend.chat.entity.Message;
import com.elice.team04backend.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class ChatController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/{issueId}")
    public Iterable<Message> selectMessages(@PathVariable int issueId) {
        return chatMessageRepository.findByIssueId(issueId);
    }
}
