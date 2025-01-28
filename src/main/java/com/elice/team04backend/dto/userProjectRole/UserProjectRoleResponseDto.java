package com.elice.team04backend.dto.userProjectRole;

import com.elice.team04backend.common.constant.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProjectRoleResponseDto {
    private Long userId;
    private String userName;
    private String email;
    private Role role;
}
