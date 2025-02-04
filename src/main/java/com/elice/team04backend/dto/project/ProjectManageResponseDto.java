package com.elice.team04backend.dto.project;

import com.elice.team04backend.dto.userProjectRole.UserProjectRoleResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectManageResponseDto {
    private ProjectResponseDto project;
    private List<UserProjectRoleResponseDto> members;
}
