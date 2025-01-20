package com.elice.team04backend.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabelUpdateDto {
    @NotBlank(message = "라벨 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "라벨 설명은 필수입니다.")
    private String description;

    //@Pattern(regexp = "^#([a-fA-F0-9]{6})$", message = "올바른 HEX 색상 코드여야 합니다.")
    @Size(min = 7, max = 7, message = "라벨 색상은 유효한 7자리 hex 코드여야 합니다.")
    private String hexCode;
}
