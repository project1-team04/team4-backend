package com.elice.team04backend.dto.project;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInviteRequestDto {

    @NotEmpty(message = "이메일을 입력하셔야 합니다.")
    private List<@Email(message = "올바른 이메일 형식이 아닙니다.") String> emails;
}
