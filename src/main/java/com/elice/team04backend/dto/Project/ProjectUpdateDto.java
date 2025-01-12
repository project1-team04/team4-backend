package com.elice.team04backend.dto.Project;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdateDto {
    private Long id;
    private String name;
}
