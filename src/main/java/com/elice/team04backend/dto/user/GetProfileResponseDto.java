package com.elice.team04backend.dto.user;

import com.elice.team04backend.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetProfileResponseDto{
    private String email;
    private String username;
    private String profileImageUrl;

    public static GetProfileResponseDto from(User user){
        return GetProfileResponseDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .profileImageUrl(user.getProfileImage())
                .build();
    }

}