package com.elice.team04backend.dto.issue;

import com.elice.team04backend.common.constant.IssueStatus;
import com.elice.team04backend.dto.issueImage.IssueImageResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueResponseDto {
    private String id;
    private Long projectId;
    private Long labelId;
    private Long assigneeUserId;
    private Long reporterUserId;
    private String issueKey;
    private String description;
    private String troubleShooting;
    private IssueStatus status;
}
