package com.ll.playon.domain.guild.guild.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGuild is a Querydsl query type for Guild
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGuild extends EntityPathBase<Guild> {

    private static final long serialVersionUID = -25197042L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGuild guild = new QGuild("guild");

    public final com.ll.playon.global.jpa.entity.QBaseTime _super = new com.ll.playon.global.jpa.entity.QBaseTime(this);

    public final EnumPath<com.ll.playon.domain.guild.guild.enums.ActiveTime> activeTime = createEnum("activeTime", com.ll.playon.domain.guild.guild.enums.ActiveTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final EnumPath<com.ll.playon.domain.guild.guild.enums.FriendType> friendType = createEnum("friendType", com.ll.playon.domain.guild.guild.enums.FriendType.class);

    public final NumberPath<Long> game = createNumber("game", Long.class);

    public final EnumPath<com.ll.playon.domain.guild.guild.enums.GameSkill> gameSkill = createEnum("gameSkill", com.ll.playon.domain.guild.guild.enums.GameSkill.class);

    public final EnumPath<com.ll.playon.domain.guild.guild.enums.GenderFilter> genderFilter = createEnum("genderFilter", com.ll.playon.domain.guild.guild.enums.GenderFilter.class);

    public final StringPath guildImg = createString("guildImg");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final NumberPath<Integer> maxMembers = createNumber("maxMembers", Integer.class);

    public final ListPath<com.ll.playon.domain.guild.guildMember.entity.GuildMember, com.ll.playon.domain.guild.guildMember.entity.QGuildMember> members = this.<com.ll.playon.domain.guild.guildMember.entity.GuildMember, com.ll.playon.domain.guild.guildMember.entity.QGuildMember>createList("members", com.ll.playon.domain.guild.guildMember.entity.GuildMember.class, com.ll.playon.domain.guild.guildMember.entity.QGuildMember.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final com.ll.playon.domain.member.entity.QMember owner;

    public final EnumPath<com.ll.playon.domain.guild.guild.enums.PartyStyle> partyStyle = createEnum("partyStyle", com.ll.playon.domain.guild.guild.enums.PartyStyle.class);

    public QGuild(String variable) {
        this(Guild.class, forVariable(variable), INITS);
    }

    public QGuild(Path<? extends Guild> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGuild(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGuild(PathMetadata metadata, PathInits inits) {
        this(Guild.class, metadata, inits);
    }

    public QGuild(Class<? extends Guild> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.owner = inits.isInitialized("owner") ? new com.ll.playon.domain.member.entity.QMember(forProperty("owner")) : null;
    }

}

