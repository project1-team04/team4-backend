package com.elice.team04backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLabel is a Querydsl query type for Label
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLabel extends EntityPathBase<Label> {

    private static final long serialVersionUID = 376569659L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLabel label = new QLabel("label");

    public final com.elice.team04backend.common.entity.QBaseEntity _super = new com.elice.team04backend.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final StringPath hexCode = createString("hexCode");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Issue, QIssue> issues = this.<Issue, QIssue>createList("issues", Issue.class, QIssue.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final QProject project;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QLabel(String variable) {
        this(Label.class, forVariable(variable), INITS);
    }

    public QLabel(Path<? extends Label> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLabel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLabel(PathMetadata metadata, PathInits inits) {
        this(Label.class, metadata, inits);
    }

    public QLabel(Class<? extends Label> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project")) : null;
    }

}

