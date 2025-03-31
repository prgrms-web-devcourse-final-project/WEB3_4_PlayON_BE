package com.ll.playon.domain.guild.guildMember.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGuildMember is a Querydsl query type for GuildMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGuildMember extends EntityPathBase<GuildMember> {

    private static final long serialVersionUID = 1151648590L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGuildMember guildMember = new QGuildMember("guildMember");

    public final com.ll.playon.global.jpa.entity.QBaseTime _super = new com.ll.playon.global.jpa.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.ll.playon.domain.guild.guild.entity.QGuild guild;

    public final EnumPath<com.ll.playon.domain.guild.guildMember.enums.GuildRole> guildRole = createEnum("guildRole", com.ll.playon.domain.guild.guildMember.enums.GuildRole.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final com.ll.playon.domain.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public QGuildMember(String variable) {
        this(GuildMember.class, forVariable(variable), INITS);
    }

    public QGuildMember(Path<? extends GuildMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGuildMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGuildMember(PathMetadata metadata, PathInits inits) {
        this(GuildMember.class, metadata, inits);
    }

    public QGuildMember(Class<? extends GuildMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.guild = inits.isInitialized("guild") ? new com.ll.playon.domain.guild.guild.entity.QGuild(forProperty("guild"), inits.get("guild")) : null;
        this.member = inits.isInitialized("member") ? new com.ll.playon.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

