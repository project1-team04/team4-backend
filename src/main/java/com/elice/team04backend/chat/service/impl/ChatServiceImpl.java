package com.elice.team04backend.chat.service.impl;

import com.elice.team04backend.chat.entity.Message;
import com.elice.team04backend.chat.handler.ChatWebSocketHandler;
import com.elice.team04backend.chat.repository.ChatMessageRepository;
import com.elice.team04backend.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashSet;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void readChat(int issueId, int userId, String userName) {
        Iterable<Message> messages = chatMessageRepository.findByIssueId(issueId);

        for (Message message : messages) {
            // 본인이 보낸 메시지는 건너뜀
            if (message.getUserId() == userId) {
                continue;
            }

            // 이미 읽은 메시지는 건너뜀
            if (message.getReadById() != null && message.getReadById().contains(userId)) {
                continue;
            }

            // 읽음 처리
            if (message.getReadById() == null) {
                message.setReadById(new HashSet<>());
            }
            if (message.getReadBy() == null) {
                message.setReadBy(new HashSet<>());
            }

            // 본인 아이디를 읽음 목록에 추가
            message.getReadById().add(String.valueOf(userId));
            // 본인 이름을 읽음 목록에 추가
            message.getReadBy().add(userName);
            // 메시지 업데이트
            chatMessageRepository.save(message);
        }
    }

}
