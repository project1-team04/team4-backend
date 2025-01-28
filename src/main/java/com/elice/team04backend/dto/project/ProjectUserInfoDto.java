package com.elice.team04backend.dto.project;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUserInfoDto {
    private String name;
    private String email;
}
