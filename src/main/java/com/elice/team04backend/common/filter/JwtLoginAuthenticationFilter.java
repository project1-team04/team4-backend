package com.elice.team04backend.common.filter;

import com.elice.team04backend.common.dto.request.SignInRequestDto;
import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.common.utils.JwtTokenProvider;
import com.elice.team04backend.common.utils.RefreshTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/*
로그인 시 토큰을 발급 & 리프레시 토큰 설정해주는 필터
 */
@Slf4j(topic = "로그인 시 토큰을 발급 & 리프레시 토큰 설정")
public class JwtLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    public JwtLoginAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RefreshTokenProvider refreshTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        setFilterProcessesUrl("/api/auth/login"); // 로그인 경로 설정
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtLoginAuthenticationFilter 필터 - attemptAuthentication");
        try {
            // 클라이언트에서 전송한 사용자 정보를 객체로 변환
            SignInRequestDto signInRequestDto = new ObjectMapper().readValue(request.getInputStream(), SignInRequestDto.class);

            // AuthenticationManager가 이메일과 비밀번호를 검증할 수 있도록 검증용 토큰을 만듦.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            signInRequestDto.getEmail(),
                            signInRequestDto.getPassword()
                    );

            // AuthenticationManager로 인증 시도
            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        log.info("JwtLoginAuthenticationFilter 필터 - successfulAuthentication");

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails.getUsername(), userDetails.getUserId(), userDetails.getName());
        String refreshToken = refreshTokenProvider.createAndStoreRefreshToken(userDetails.getUserId());

        // 리프레시 토큰을 쿠키로 설정
        setRefreshTokenCookie(response, refreshToken);

        // 액세스 토큰 JWT를 클라이언트에 반환
        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\"}");
        response.getWriter().flush();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) (refreshTokenProvider.getRefreshTokenExpiration() * 60 * 60); // 초 단위로 변환

        // 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 접근 불가
        refreshTokenCookie.setPath("/");     // 애플리케이션 전체에서 사용 가능
        refreshTokenCookie.setMaxAge(cookieMaxAge); // 쿠키 유효 기간 설정

        // 쿠키 추가
        response.addCookie(refreshTokenCookie);
    }

}
