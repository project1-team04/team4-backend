package com.elice.team04backend.dto.project;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDto implements Serializable{

    private String id;
    private String projectKey;
    private String name;
    private Long issueCount;
}
