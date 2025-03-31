package com.ll.playon.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberSteamData is a Querydsl query type for MemberSteamData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberSteamData extends EntityPathBase<MemberSteamData> {

    private static final long serialVersionUID = 1398276921L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberSteamData memberSteamData = new QMemberSteamData("memberSteamData");

    public final com.ll.playon.global.jpa.entity.QBaseEntity _super = new com.ll.playon.global.jpa.entity.QBaseEntity(this);

    public final NumberPath<Long> appId = createNumber("appId", Long.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final QMember member;

    public QMemberSteamData(String variable) {
        this(MemberSteamData.class, forVariable(variable), INITS);
    }

    public QMemberSteamData(Path<? extends MemberSteamData> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberSteamData(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberSteamData(PathMetadata metadata, PathInits inits) {
        this(MemberSteamData.class, metadata, inits);
    }

    public QMemberSteamData(Class<? extends MemberSteamData> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

