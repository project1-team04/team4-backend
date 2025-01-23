package com.elice.team04backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 860083616L;

    public static final QProject project = new QProject("project");

    public final com.elice.team04backend.common.entity.QBaseEntity _super = new com.elice.team04backend.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> issueCount = createNumber("issueCount", Long.class);

    public final ListPath<Issue, QIssue> issues = this.<Issue, QIssue>createList("issues", Issue.class, QIssue.class, PathInits.DIRECT2);

    public final ListPath<Label, QLabel> labels = this.<Label, QLabel>createList("labels", Label.class, QLabel.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath projectKey = createString("projectKey");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<UserProjectRole, QUserProjectRole> userProjectRoles = this.<UserProjectRole, QUserProjectRole>createList("userProjectRoles", UserProjectRole.class, QUserProjectRole.class, PathInits.DIRECT2);

    public QProject(String variable) {
        super(Project.class, forVariable(variable));
    }

    public QProject(Path<? extends Project> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProject(PathMetadata metadata) {
        super(Project.class, metadata);
    }

}

