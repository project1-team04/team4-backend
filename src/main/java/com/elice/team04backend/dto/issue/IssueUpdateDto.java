package com.elice.team04backend.dto.issue;

import com.elice.team04backend.common.constant.IssueStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueUpdateDto {
    private String description;
    private String troubleShooting;
    private IssueStatus status;
}
