package com.elice.team04backend.common.config;

import com.elice.team04backend.common.filter.JwtAuthenticationFilter;
import com.elice.team04backend.common.filter.JwtLoginAuthenticationFilter;
import com.elice.team04backend.common.filter.ProjectRoleAuthorizationFilter;
import com.elice.team04backend.common.utils.JwtTokenProvider;
import com.elice.team04backend.service.UserProjectRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;

    private final JwtLoginAuthenticationFilter jwtLoginAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ProjectRoleAuthorizationFilter projectRoleAuthorizationFilter;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserProjectRoleService userProjectRoleService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/login").permitAll()
                            .requestMatchers("/api/auth/signup").permitAll()
                            .anyRequest().authenticated()
                )
                .addFilterBefore(jwtLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(projectRoleAuthorizationFilter, JwtAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public ProjectRoleAuthorizationFilter projectRoleAuthorizationFilter(){
        return new ProjectRoleAuthorizationFilter(jwtTokenProvider, userProjectRoleService);
    }

    @Bean
    public JwtLoginAuthenticationFilter jwtLoginAuthenticationFilter() {
        return new JwtLoginAuthenticationFilter(authenticationManager, jwtTokenProvider);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000"); // 클라이언트 도메인에 해당하는 cors 설정
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

