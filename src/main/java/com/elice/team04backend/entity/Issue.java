package com.elice.team04backend.entity;

import com.elice.team04backend.common.constant.IssueStatus;
import com.elice.team04backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Issue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "label_id", nullable = false)
    private Label labelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "project_id", nullable = false)
    private Project projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "assignee_user_id", nullable = false)
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "reporter_user_id", nullable = false)
    private User reporter;

    @Column(name = "issue_key", nullable = false)
    private Long issueKey;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "trouble_shooting", columnDefinition = "TEXT")
    private String troubleShooting;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private IssueStatus status;
}
