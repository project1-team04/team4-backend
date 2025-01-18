package com.elice.team04backend.entity;

import com.elice.team04backend.common.constant.IssueStatus;
import com.elice.team04backend.common.entity.BaseEntity;
import com.elice.team04backend.dto.issue.IssueResponseDto;
import com.elice.team04backend.dto.issue.IssueUpdateDto;
import com.elice.team04backend.dto.issueImage.IssueImageResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @JoinColumn(name = "label_id", nullable = false)
    private Label label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "assignee_user_id", nullable = false)
    @JoinColumn(name = "assignee_user_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporter;

    @Column(name = "issue_key")
    private String issueKey;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "trouble_shooting", columnDefinition = "TEXT")
    private String troubleShooting;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueImage> issueImages = new ArrayList<>();

    public void update(IssueUpdateDto issueUpdateDto) {
        this.description = issueUpdateDto.getDescription();
        this.troubleShooting = issueUpdateDto.getTroubleShooting();
    }

    public void addIssueImages(IssueImage issueImage) {
        if (this.getIssueImages() == null) {
            this.issueImages = new ArrayList<>();
        }
        issueImage.setIssue(this);
        this.getIssueImages().add(issueImage);
    }

    public IssueResponseDto from() {
        return IssueResponseDto.builder()
                .id(String.valueOf(this.id))
                .projectId(this.getProject().getId())
                .labelId(this.getLabel().getId())
                //.assigneeUserId(this.getAssignee().getId())
                .reporterUserId(this.getReporter().getId())
                .issueKey(this.getIssueKey())
                .description(this.getDescription())
                .troubleShooting(this.getTroubleShooting())
                .status(this.getStatus())
                .build();
    }
}
