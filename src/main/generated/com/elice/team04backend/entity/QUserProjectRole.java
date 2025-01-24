package com.elice.team04backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserProjectRole is a Querydsl query type for UserProjectRole
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserProjectRole extends EntityPathBase<UserProjectRole> {

    private static final long serialVersionUID = 817503723L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserProjectRole userProjectRole = new QUserProjectRole("userProjectRole");

    public final com.elice.team04backend.common.entity.QBaseEntity _super = new com.elice.team04backend.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProject project;

    public final EnumPath<com.elice.team04backend.common.constant.Role> role = createEnum("role", com.elice.team04backend.common.constant.Role.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUser user;

    public QUserProjectRole(String variable) {
        this(UserProjectRole.class, forVariable(variable), INITS);
    }

    public QUserProjectRole(Path<? extends UserProjectRole> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserProjectRole(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserProjectRole(PathMetadata metadata, PathInits inits) {
        this(UserProjectRole.class, metadata, inits);
    }

    public QUserProjectRole(Class<? extends UserProjectRole> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

