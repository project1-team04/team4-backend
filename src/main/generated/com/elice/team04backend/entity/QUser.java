package com.elice.team04backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 150980260L;

    public static final QUser user = new QUser("user");

    public final com.elice.team04backend.common.entity.QBaseEntity _super = new com.elice.team04backend.common.entity.QBaseEntity(this);

    public final ListPath<Issue, QIssue> assigneeIssues = this.<Issue, QIssue>createList("assigneeIssues", Issue.class, QIssue.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expirationAt = createDateTime("expirationAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isVerified = createBoolean("isVerified");

    public final StringPath password = createString("password");

    public final StringPath profileImage = createString("profileImage");

    public final EnumPath<com.elice.team04backend.common.constant.Provider> provider = createEnum("provider", com.elice.team04backend.common.constant.Provider.class);

    public final StringPath providerId = createString("providerId");

    public final StringPath refreshToken = createString("refreshToken");

    public final ListPath<Issue, QIssue> reporterIssues = this.<Issue, QIssue>createList("reporterIssues", Issue.class, QIssue.class, PathInits.DIRECT2);

    public final EnumPath<com.elice.team04backend.common.constant.UserStatus> status = createEnum("status", com.elice.team04backend.common.constant.UserStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath username = createString("username");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

