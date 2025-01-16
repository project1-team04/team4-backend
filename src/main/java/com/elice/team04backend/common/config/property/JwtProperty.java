package com.elice.team04backend.common.config.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {
    private String secret;
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;
}
