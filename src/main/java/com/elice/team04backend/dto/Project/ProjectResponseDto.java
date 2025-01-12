package com.elice.team04backend.dto.Project;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDto {

    private Long id;

    private String projectKey;

    private String name;

    private Long issueCount;
}
