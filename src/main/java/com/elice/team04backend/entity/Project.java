package com.elice.team04backend.entity;

import com.elice.team04backend.common.entity.BaseEntity;
import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.dto.project.ProjectUpdateDto;
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
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", nullable = false)
    private Long id;

    @Column(name = "project_key", nullable = false)
    private String projectKey;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "issue_count", nullable = false)
    private Long issueCount;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProjectRole> userProjectRoles = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Label> labels = new ArrayList<>();

    public void update(ProjectUpdateDto projectUpdateDto) {
        this.name = projectUpdateDto.getName();
    }

    public void addIssue(Issue issue) {
        this.issues.add(issue);
        this.issueCount = (long) this.issues.size();
    }

    public void addLabel(Label label) {
        this.labels.add(label);
    }

    public ProjectResponseDto from() {
        return ProjectResponseDto.builder()
                .id(this.getId())
                .projectKey(this.getProjectKey())
                .issueCount(this.getIssueCount())
                .name(this.getName())
                .build();
    }
}
