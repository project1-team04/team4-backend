package com.elice.team04backend.common.utils;

import com.elice.team04backend.common.config.property.JwtProperty;
import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j(topic = "토큰 생성 및 검증")
@Component
public class JwtTokenProvider {

    private final JwtProperty jwtProperty;
    private final Key secretKey;

    public JwtTokenProvider(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(jwtProperty.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(secretByteKey);
    }

    // Token 생성

    public String generateAccessToken(String email, Long userId) {
        return generateToken(email, userId, jwtProperty.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String email, Long userId) {
        return generateToken(email, userId, jwtProperty.getRefreshTokenExpiration());
    }

    private String generateToken(String email, Long userId, long expirationTime) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Token 정보 읽기 & 인증

    public Long getRefreshTokenExpiration() {
        return jwtProperty.getRefreshTokenExpiration();
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

        return new UsernamePasswordAuthenticationToken(new UserDetailsImpl(user), null);
    }

    // Token 검증

    public boolean validateToken(String token) {
        try {
            // 서명 검증 및 토큰 파싱
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // SecretKey로 서명 검증
                    .build()
                    .parseClaimsJws(token); // 유효하지 않으면 예외 발생
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
}
