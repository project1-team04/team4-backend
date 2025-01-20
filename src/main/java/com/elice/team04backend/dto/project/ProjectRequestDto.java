package com.elice.team04backend.dto.project;

import com.elice.team04backend.entity.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestDto {

    @NotBlank(message = "프로젝트 이름은 필수입니다.")
    private String name;

    private List<String> emails;

    public Project from(String projectKey) {
        return Project.builder()
                .name(this.getName())
                .projectKey(projectKey)
                .issueCount(0L)
                .build();
    }
}
