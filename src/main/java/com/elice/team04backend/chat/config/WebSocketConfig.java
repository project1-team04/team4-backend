package com.elice.team04backend.chat.config;


import com.elice.team04backend.chat.handler.ChatWebSocketHandler;
import com.elice.team04backend.chat.repository.ChatMessageRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 생성자 주입을 통해 ChatMessageRepository와 RedisTemplate을 주입받습니다.
    public WebSocketConfig(ChatMessageRepository chatMessageRepository, RedisTemplate<String, Object> redisTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket 경로를 "/chat/{issueId}"로 변경
        registry.addHandler(new ChatWebSocketHandler(chatMessageRepository, redisTemplate), "/api/chat/{issueId}")
                .setAllowedOrigins(
                        "http://localhost:3000",
                        "http://34.22.102.28:8080"
                );
    }
}
