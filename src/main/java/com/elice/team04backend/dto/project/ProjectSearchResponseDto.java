package com.elice.team04backend.dto.project;

import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSearchResponseDto {
    private List<ProjectResponseDto> projects;
    private long conditionTotalProjects;
    private long totalProjects;
}
