package com.elice.team04backend.chat.service.impl;

import com.elice.team04backend.chat.SessionRegistry;
import com.elice.team04backend.chat.entity.Message;
import com.elice.team04backend.chat.repository.ChatMessageRepository;
import com.elice.team04backend.chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;


    @Autowired
    public ChatServiceImpl(RedisTemplate<String, Object> redisTemplate, ChatMessageRepository chatMessageRepository) {
        this.redisTemplate = redisTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public Iterable<Message> readChat(int issueId, int userId, String userName) {
        Iterable<Message> messages = chatMessageRepository.findByIssueId(issueId);
        List<Message> updateMessages = new ArrayList<>();

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
            updateMessages.add(message);
        }
        sendMessage(issueId, updateMessages);
        return updateMessages;
    }

    public void sendMessage(int issueId, List<Message> messages) {
        try{
        String redisKey = "chat:session:issue:" + issueId;
        Set<Object> members = redisTemplate.opsForSet().members(redisKey);
        Set<String> sessionIds = convertToStringSet(members);

        if (sessionIds != null && !sessionIds.isEmpty()) {
            // 메시지 리스트를 JSON 문자열로 변환
            for(Message message : messages){
                String jsonMessages = objectMapper.writeValueAsString(message);
                TextMessage textMessage = new TextMessage(jsonMessages);

                for (String sessionId : sessionIds) {
                    WebSocketSession session = SessionRegistry.getSession(sessionId);
                    if (session != null && session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                }
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //오브젝트 타입을 문자열로 변환
    private Set<String> convertToStringSet(Set<Object> objectSet) {
        if (objectSet == null) {
            return new HashSet<>();
        }
        return objectSet.stream()
                .filter(obj -> obj != null)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

}
