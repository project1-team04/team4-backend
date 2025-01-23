package com.elice.team04backend.dto.issue;

import com.elice.team04backend.common.constant.IssueStatus;
import com.elice.team04backend.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueRequestDto {

    private Long labelId;

    @NotNull(message = "이슈 제목을 입력해주세요.")
    private String name;

    @NotNull(message = "담당자 ID는 필수입니다.")
    private Long assigneeUserId;

    @NotBlank(message = "이슈 설명은 필수입니다.")
    private String description;

    private String troubleShooting;

    @NotNull(message = "이슈 상태는 필수입니다.")
    private IssueStatus status;


    public Issue from(User reporter, User assignee, Project project, Label label, String generatedIssueKey) {
        return Issue.builder()
                .project(project)
                .label(label)
                .reporter(reporter)
                .assignee(assignee)
                .issueKey(generatedIssueKey)
                .name(this.name)
                .description(this.getDescription())
                .troubleShooting(this.getTroubleShooting())
                .status(this.getStatus())
                .build();

    }
}
