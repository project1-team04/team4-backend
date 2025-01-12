package com.elice.team04backend.dto.Project;

import com.elice.team04backend.entity.Project;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestDto {

    private String name;

    public Project from() {
        return Project.builder()
                .name(this.name)
                .build();
    }
}
