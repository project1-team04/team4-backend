package com.elice.team04backend.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NaverAccountResponseDto {

    private String resultcode;
    private String message;
    private Response response;

    @Data
    public static class Response {
        private String email;

        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        private String gender;

        private String id;

        private String name;

        private String birthday;
    }
}

