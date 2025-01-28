package com.elice.team04backend.dto.project;

import lombok.*;

import java.io.Serializable;
import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTotalResponseDto implements Serializable{
    private List<ProjectResponseDto> projects;
    private long totalProjects;
}
