package com.elice.team04backend.dto.issue;

import com.elice.team04backend.common.constant.IssueStatus;
import com.elice.team04backend.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueRequestDto {
    //@NotNull(message = "프로젝트 ID는 필수입니다.")
    private Long projectId;

    //@NotNull(message = "라벨 ID는 필수입니다.")
    private Long labelId;

    //@NotNull(message = "담당자 ID는 필수입니다.")
    private Long assigneeUserId;

    //@NotNull(message = "작성자 ID는 필수입니다.")
    private Long reporterUserId;

    //@NotBlank(message = "이슈 키는 필수입니다.")
    //private Long issueKey;
    //private String issueKey;

    @NotBlank(message = "이슈 설명은 필수입니다.")
    private String description;

    private String troubleShooting;

    //@NotBlank(message = "이슈 상태는 필수입니다.")
    @NotNull(message = "이슈 상태는 필수입니다.") // enum 타입 에는 blank 사용하면 에러발생
    private IssueStatus status;

    private List<IssueImage> issueImages;

//    public Issue from(Project project, User assignee, User reporter, String issueKey) {
//        return Issue.builder()
//                .project(project)
//                .assignee(assignee)
//                .reporter(reporter)
//                .issueKey(issueKey)
//                .description(description)
//                .troubleShooting(troubleShooting)
//                .status(status)
//                .build();
//    }

    public Issue from(Project project, Label label, String generatedIssueKey) {
        Issue issue = Issue.builder()
                .project(project)
                .label(label)
                .issueKey(generatedIssueKey)
                .description(this.getDescription())
                .troubleShooting(this.getTroubleShooting())
                .status(this.getStatus())
                .build();
        if (this.issueImages != null) {
            this.issueImages.forEach(url -> {
                IssueImage image = new IssueImage();
                image.setImageUrl(String.valueOf(url));
                image.setIssue(issue);
                issue.addIssueImages(image);
            });
        }
        return issue;
    }
}
