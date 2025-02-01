package com.elice.team04backend.chat.config;


import com.elice.team04backend.chat.handler.ChatWebSocketHandler;
import com.elice.team04backend.chat.repository.ChatMessageRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.elice.team04backend.common.utils.JwtTokenProvider;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

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
        registry.addHandler(new ChatWebSocketHandler(chatMessageRepository, redisTemplate), "/api/chat/{issueId}")
                .setAllowedOrigins("*");
                //.addInterceptors(new WebSocketInterceptor(jwtTokenProvider)); // 인터셉터 추가
    }
/*
    // 웹소켓 인터셉터 추가
    public class WebSocketInterceptor implements HandshakeInterceptor {

        private final JwtTokenProvider jwtTokenProvider;

        public WebSocketInterceptor(JwtTokenProvider jwtTokenProvider) {
            this.jwtTokenProvider = jwtTokenProvider;
        }

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) {
            // 프로토콜에서 토큰 추출
            List<String> protocols = request.getHeaders().get("Sec-WebSocket-Protocol");
            if (protocols != null && !protocols.isEmpty()) {
                String token = protocols.get(0);

                // 토큰 검증
                try {
                    if (jwtTokenProvider.validateToken(token)) {
                        // 검증된 사용자 정보를 속성에 저장
                        String userId = jwtTokenProvider.getUserPk(token);
                        attributes.put("userId", userId);

                        // 응답 헤더에 프로토콜 설정
                        response.getHeaders().set("Sec-WebSocket-Protocol", token);
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) {
        }
    }*/
}



