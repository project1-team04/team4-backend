package com.elice.team04backend.chat.handler;

import com.elice.team04backend.chat.entity.Message;
import com.elice.team04backend.chat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;
    private final Map<Integer, Set<WebSocketSession>> sessionsByIssue = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = new ObjectMapper();

        // JavaTimeModule 등록
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("------------------------------------------------");
        System.out.println("받은 메시지 : " + payload);
        System.out.println("------------------------------------------------");

        // JSON -> MessageVo 객체 변환
        Message messageVo = objectMapper.readValue(payload, Message.class);

        // 메시지에 시간 설정
        if (messageVo.getTimestamp() == null) {
            messageVo.setTimestamp(LocalDateTime.now());
        }

        // 채팅 메시지 저장
        chatMessageRepository.save(messageVo);

        // 이슈 ID를 통해 해당 이슈에 연결된 세션들 찾기
        int issueId = Integer.parseInt(messageVo.getIssueId());
        Set<WebSocketSession> sessions = sessionsByIssue.computeIfAbsent(issueId, k -> new HashSet<>());

        System.out.println("브로드캐스트할 세션들: " + sessions.size() + "개 세션");

        // 해당 이슈에 연결된 클라이언트들에게 메시지 브로드캐스트
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageVo)));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        // 클라이언트가 연결되면 해당 이슈 ID를 URL에서 가져와 세션에 추가
        String issueIdString = session.getUri().getPath().split("/")[2];  // 이슈 ID를 URL에서 가져오기
        int issueId = Integer.parseInt(issueIdString);

        sessionsByIssue.computeIfAbsent(issueId, k -> new HashSet<>()).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // 연결 종료 시 세션을 해당 이슈 그룹에서 제거
        sessionsByIssue.values().forEach(sessions -> sessions.remove(session));
        super.afterConnectionClosed(session, status);
    }
}
