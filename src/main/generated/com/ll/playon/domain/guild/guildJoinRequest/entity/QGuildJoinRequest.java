package com.ll.playon.domain.guild.guildJoinRequest.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGuildJoinRequest is a Querydsl query type for GuildJoinRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGuildJoinRequest extends EntityPathBase<GuildJoinRequest> {

    private static final long serialVersionUID = -831719252L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGuildJoinRequest guildJoinRequest = new QGuildJoinRequest("guildJoinRequest");

    public final com.ll.playon.global.jpa.entity.QBaseTime _super = new com.ll.playon.global.jpa.entity.QBaseTime(this);

    public final EnumPath<com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState> approvalState = createEnum("approvalState", com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState.class);

    public final com.ll.playon.domain.member.entity.QMember approvedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.ll.playon.domain.guild.guild.entity.QGuild guild;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.ll.playon.domain.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public QGuildJoinRequest(String variable) {
        this(GuildJoinRequest.class, forVariable(variable), INITS);
    }

    public QGuildJoinRequest(Path<? extends GuildJoinRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGuildJoinRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGuildJoinRequest(PathMetadata metadata, PathInits inits) {
        this(GuildJoinRequest.class, metadata, inits);
    }

    public QGuildJoinRequest(Class<? extends GuildJoinRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.approvedBy = inits.isInitialized("approvedBy") ? new com.ll.playon.domain.member.entity.QMember(forProperty("approvedBy")) : null;
        this.guild = inits.isInitialized("guild") ? new com.ll.playon.domain.guild.guild.entity.QGuild(forProperty("guild"), inits.get("guild")) : null;
        this.member = inits.isInitialized("member") ? new com.ll.playon.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

