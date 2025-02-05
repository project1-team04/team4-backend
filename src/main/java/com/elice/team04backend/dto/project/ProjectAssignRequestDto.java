package com.elice.team04backend.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAssignRequestDto {
    private Long projectId;
    private Long userId;
}
