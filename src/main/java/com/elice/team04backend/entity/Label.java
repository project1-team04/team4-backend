package com.elice.team04backend.entity;

import com.elice.team04backend.common.entity.BaseEntity;
import com.elice.team04backend.dto.label.LabelResponseDto;
import com.elice.team04backend.dto.label.LabelUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Label extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "hexcode", nullable = false, length = 7)
    private String hexCode;

    @OneToMany(mappedBy = "label")
    private List<Issue> issues = new ArrayList<>();

    public void update(LabelUpdateDto labelUpdateDto) {
        this.name = labelUpdateDto.getName();
        this.description = labelUpdateDto.getDescription();
        this.hexCode = labelUpdateDto.getHexCode();
    }

    public LabelResponseDto from() {
        return LabelResponseDto.builder()
                .id(this.getId())
                .name(this.getName())
                .description(this.getDescription())
                .hexCode(this.getHexCode())
                .build();
    }

}
