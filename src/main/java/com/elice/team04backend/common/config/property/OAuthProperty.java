package com.elice.team04backend.common.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class OAuthProperty {

    private Google google;
    private Kakao kakao;
    private Naver naver;

    @Data
    public static class Google {
        private String oauthUri;
        private String clientId;
        private String clientSecret;
        private String accessScope;
        private String redirectUri;
        private String grantType;
    }

    @Data
    public static class Kakao {
        private String oauthUri;
        private String clientId;
        private String redirectUri;
        private String grantType;
    }

    @Data
    public static class Naver {
        private String oauthUri;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String grantType;
    }
}

