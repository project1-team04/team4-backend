package com.elice.team04backend.common.filter;

import com.elice.team04backend.common.utils.JwtTokenProvider;
import com.elice.team04backend.common.utils.UrlUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/*
Jwt 검증 및 SpringContext에 인증 객체 등록
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return Arrays.stream(UrlUtils.PermittedUrl).anyMatch(authPath -> pathMatcher.match(authPath, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JwtAuthenticationFilter 필터 검증");

        String token = jwtTokenProvider.resolveAccessToken(request);

        if (token == null) {
            log.info("액세스 토큰이 필요합니다.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}