package com.elice.team04backend.common.filter;

import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.common.utils.JwtTokenProvider;
import com.elice.team04backend.service.UserProjectRoleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
Project에 대해 권한 처리를 하는 필터
 */
@Slf4j
public class ProjectRoleAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserProjectRoleService userProjectRoleService;

    public ProjectRoleAuthorizationFilter(JwtTokenProvider jwtTokenProvider, UserProjectRoleService userProjectRoleService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userProjectRoleService = userProjectRoleService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/project"); // 특정 경로(`/api/project/**`)에만 필터 동작
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String projectId = request.getParameter("projectId"); // GET /api/project?projectId=12345 이렇게 적힌 경우, 12345를 가져옴.

        if (authentication != null && projectId != null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            String projectRole = userProjectRoleService.getUserRoleForProject(userId, projectId);

            if (projectRole == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }

            // 기존 인증 객체에 프로젝트 권한 추가
            List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + projectRole));
            Authentication updatedAuth = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(updatedAuth);
        }

        log.info("ProjectRoleAuthorizationFilter check========");
        filterChain.doFilter(request, response);
    }
}

