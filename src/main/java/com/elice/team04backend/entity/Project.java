package com.elice.team04backend.entity;

import com.elice.team04backend.common.entity.BaseEntity;
import com.elice.team04backend.dto.Project.ProjectResponseDto;
import com.elice.team04backend.dto.Project.ProjectUpdateDto;
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

    @OneToMany(mappedBy = "project")
    private List<UserProjectRole> userProjectRoles = new ArrayList<>();

    @OneToMany(mappedBy = "project", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Issue> issues = new ArrayList<>();

    public void updateName(ProjectUpdateDto projectUpdateDto) {
        this.name = projectUpdateDto.getName();
    }

    public ProjectResponseDto from() {
        return ProjectResponseDto.builder()
                .id(this.id)
                .projectKey(this.projectKey)
                .issueCount(this.issueCount)
                .name(this.name)
                .build();

    }
}
