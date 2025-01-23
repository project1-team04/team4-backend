package com.elice.team04backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIssue is a Querydsl query type for Issue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIssue extends EntityPathBase<Issue> {

    private static final long serialVersionUID = 374352160L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIssue issue = new QIssue("issue");

    public final com.elice.team04backend.common.entity.QBaseEntity _super = new com.elice.team04backend.common.entity.QBaseEntity(this);

    public final QUser assignee;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<IssueImage, QIssueImage> issueImages = this.<IssueImage, QIssueImage>createList("issueImages", IssueImage.class, QIssueImage.class, PathInits.DIRECT2);

    public final StringPath issueKey = createString("issueKey");

    public final QLabel label;

    public final StringPath name = createString("name");

    public final QProject project;

    public final QUser reporter;

    public final EnumPath<com.elice.team04backend.common.constant.IssueStatus> status = createEnum("status", com.elice.team04backend.common.constant.IssueStatus.class);

    public final StringPath troubleShooting = createString("troubleShooting");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QIssue(String variable) {
        this(Issue.class, forVariable(variable), INITS);
    }

    public QIssue(Path<? extends Issue> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIssue(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIssue(PathMetadata metadata, PathInits inits) {
        this(Issue.class, metadata, inits);
    }

    public QIssue(Class<? extends Issue> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.assignee = inits.isInitialized("assignee") ? new QUser(forProperty("assignee")) : null;
        this.label = inits.isInitialized("label") ? new QLabel(forProperty("label"), inits.get("label")) : null;
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project")) : null;
        this.reporter = inits.isInitialized("reporter") ? new QUser(forProperty("reporter")) : null;
    }

}

