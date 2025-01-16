package com.elice.team04backend.entity;

import com.elice.team04backend.common.entity.BaseEntity;
import com.elice.team04backend.dto.issueImage.IssueImageResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class IssueImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_image_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @Column(name = "image_url")
    private String imageUrl;

    IssueImageResponseDto from() {
        return IssueImageResponseDto.builder()
                .id(this.getId())
                .imageUrl(this.getImageUrl())
                .build();
    }

}
