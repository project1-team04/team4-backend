package com.elice.team04backend.chat.handler;

import com.elice.team04backend.chat.SessionRegistry;
import com.elice.team04backend.chat.entity.Message;
import com.elice.team04backend.chat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_SESSION_KEY_PREFIX = "chat:session:issue:";

    public ChatWebSocketHandler(
            ChatMessageRepository chatMessageRepository,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Message messageVo = objectMapper.readValue(payload, Message.class);

        if (messageVo.getTimestamp() == null) {
            messageVo.setTimestamp(LocalDateTime.now());
        }

        chatMessageRepository.save(messageVo);

        // Redis에서 해당 이슈의 모든 세션 ID를 가져와서 메시지 전송
        String redisKey = REDIS_SESSION_KEY_PREFIX + messageVo.getIssueId();
        //해당 이슈 ID와 관련된 모든 세션 ID를 Redis에서 가져온다
        Set<Object> members = redisTemplate.opsForSet().members(redisKey);
        Set<String> sessionIds = convertToStringSet(members);

        if (sessionIds != null) {
            String messageJson = objectMapper.writeValueAsString(messageVo);
            for (String sessionId : sessionIds) {
                WebSocketSession webSocketSession = SessionRegistry.getSession(sessionId);
                if (webSocketSession != null && webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(messageJson));
                }
            }
        }
    }

    private Set<String> convertToStringSet(Set<Object> objectSet) {
        if (objectSet == null) {
            return new HashSet<>();
        }
        return objectSet.stream()
                .filter(obj -> obj != null)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        String issueIdString = session.getUri().getPath().split("/")[2];
        int issueId = Integer.parseInt(issueIdString);
        String redisKey = REDIS_SESSION_KEY_PREFIX + issueId;

        // Redis에 세션 ID 저장
        redisTemplate.opsForSet().add(redisKey, session.getId());

        // 세션 레지스트리에 세션 저장
        SessionRegistry.addSession(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String issueIdString = session.getUri().getPath().split("/")[2];
        int issueId = Integer.parseInt(issueIdString);
        String redisKey = REDIS_SESSION_KEY_PREFIX + issueId;

        // Redis에서 세션 ID 제거
        redisTemplate.opsForSet().remove(redisKey, session.getId());

        // 세션 레지스트리에서 세션 제거
        SessionRegistry.removeSession(session.getId());

        super.afterConnectionClosed(session, status);
    }
}