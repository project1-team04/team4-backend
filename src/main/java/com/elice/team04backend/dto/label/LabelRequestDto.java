package com.elice.team04backend.dto.label;

import com.elice.team04backend.entity.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelRequestDto {
    @NotBlank(message = "라벨 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "라벨 설명은 필수입니다.")
    private String description;

    //@Pattern(regexp = "^#([a-fA-F0-9]{6})$", message = "올바른 HEX 색상 코드여야 합니다.")
    @Size(min = 7, max = 7, message = "라벨 색상은 유효한 7자리 hex 코드여야 합니다.")
    private String hexCode;

    public Label from() {
        return Label.builder()
                .name(this.getName())
                .description(this.getDescription())
                .hexCode(this.getHexCode())
                .build();
    }
}
