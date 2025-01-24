package com.elice.team04backend.common.dto.request;

public record ChangePasswordRequestDto (
        String oldPassword,
        String newPassword
){}
