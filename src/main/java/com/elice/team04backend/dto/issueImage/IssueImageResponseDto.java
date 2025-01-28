package com.elice.team04backend.dto.issueImage;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueImageResponseDto {
    private String id;
    private String imageUrl;
    private String originalName;

}
