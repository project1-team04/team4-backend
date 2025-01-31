package com.elice.team04backend.common.utils;

import com.elice.team04backend.common.config.property.TokenProperty;
import com.elice.team04backend.common.model.RedisDAO;
import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j(topic = "토큰 생성 및 검증")
@Component
public class JwtTokenProvider {

    private final TokenProperty tokenProperty;
    private final Key secretKey;
    private final RedisDAO redisDAO;

    public JwtTokenProvider(TokenProperty tokenProperty, RedisDAO redisDAO) {
        this.tokenProperty = tokenProperty;
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(tokenProperty.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(secretByteKey);
        this.redisDAO = redisDAO;
    }

    // Token 생성

    public String generateAccessToken(String email, Long userId, String username) {
        return generateToken(email, userId, username, tokenProperty.getAccessTokenExpiration());
    }

    private String generateToken(String email, Long userId, String username,  long expirationTime) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("username", username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    // Token으로부터 인증 생성

    public Authentication getAuthentication(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        User user = User.builder()
                .id(userId)
                .email(email)
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // Token 검증

    public boolean validateToken(String token) {

        if (!ObjectUtils.isEmpty(redisDAO.getValues(token))) {
            log.info("사용했던 accessToken입니다.");
            return false;
        }

        try {
            // 서명 검증 및 토큰 파싱
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // SecretKey로 서명 검증
                    .build()
                    .parseClaimsJws(token); // 유효하지 않으면 예외 발생
            log.info("검증 성공 token : {}", token);
            return true; // 검증 성공
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다.");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.info("유효하지 않은 토큰 형식입니다.");
        } catch (io.jsonwebtoken.SignatureException e) {
            log.info("토큰 서명이 유효하지 않습니다.");
        } catch (Exception e) {
            log.info("토큰 검증 중 알 수 없는 오류 발생: " + e.getMessage());
        }
        return false; // 검증 실패
    }

    // AccessToken 만료시간 확인

    public Long getAccessTokenExpiration() {
        return tokenProperty.getAccessTokenExpiration();
    }

    // AccessToken 추출

    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return (bearerToken != null && bearerToken.startsWith("Bearer "))
                ? bearerToken.substring(7)
                : null;
    }

    public String getUserPk(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = claims.get("userId", Long.class); // "userId" 클레임으로 사용자 ID 추출
            return userId != null ? String.valueOf(userId) : null; // Long을 String으로 변환
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }
}
