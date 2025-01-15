package com.elice.team04backend.dto.label;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelResponseDto {
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private String hexCode;
}
