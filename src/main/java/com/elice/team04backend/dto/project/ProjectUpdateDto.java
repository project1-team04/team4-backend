package com.elice.team04backend.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdateDto {

    @NotBlank(message = "프로젝트 이름은 필수입니다.")
    private String name;

    private List<String> emails;
}
