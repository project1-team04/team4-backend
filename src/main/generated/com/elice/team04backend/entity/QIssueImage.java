package com.elice.team04backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIssueImage is a Querydsl query type for IssueImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIssueImage extends EntityPathBase<IssueImage> {

    private static final long serialVersionUID = -1925954533L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIssueImage issueImage = new QIssueImage("issueImage");

    public final com.elice.team04backend.common.entity.QBaseEntity _super = new com.elice.team04backend.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final QIssue issue;

    public final StringPath originalName = createString("originalName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QIssueImage(String variable) {
        this(IssueImage.class, forVariable(variable), INITS);
    }

    public QIssueImage(Path<? extends IssueImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIssueImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIssueImage(PathMetadata metadata, PathInits inits) {
        this(IssueImage.class, metadata, inits);
    }

    public QIssueImage(Class<? extends IssueImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.issue = inits.isInitialized("issue") ? new QIssue(forProperty("issue"), inits.get("issue")) : null;
    }

}

