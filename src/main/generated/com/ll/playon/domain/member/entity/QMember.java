package com.ll.playon.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1980323553L;

    public static final QMember member = new QMember("member1");

    public final com.ll.playon.global.jpa.entity.QBaseTime _super = new com.ll.playon.global.jpa.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final ListPath<MemberSteamData, QMemberSteamData> games = this.<MemberSteamData, QMemberSteamData>createList("games", MemberSteamData.class, QMemberSteamData.class, PathInits.DIRECT2);

    public final EnumPath<Gender> gender = createEnum("gender", Gender.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath is_deleted = createBoolean("is_deleted");

    public final DateTimePath<java.time.LocalDateTime> lastLoginAt = createDateTime("lastLoginAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final EnumPath<PlayStyle> play_style = createEnum("play_style", PlayStyle.class);

    public final EnumPath<PreferredGenres> preferred_genres = createEnum("preferred_genres", PreferredGenres.class);

    public final StringPath profile_img = createString("profile_img");

    public final NumberPath<Long> steamId = createNumber("steamId", Long.class);

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

