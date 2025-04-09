package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.entity.QGuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.member.entity.QMember;
import com.ll.playon.domain.title.entity.QMemberTitle;
import com.ll.playon.domain.title.entity.QTitle;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GuildMemberRepositoryCustomImpl implements GuildMemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<GuildMember> findTopNByGuildOrderByRoleAndCreatedAt(Guild guild, int limit) {
        QGuildMember gm = QGuildMember.guildMember;
        QMember m = QMember.member;
        QMemberTitle mt = QMemberTitle.memberTitle;
        QTitle t = QTitle.title;

        return queryFactory
                .selectFrom(gm)
                .leftJoin(gm.member, m).fetchJoin()
                .leftJoin(m.memberTitles, mt).fetchJoin()
                .leftJoin(mt.title, t).fetchJoin()
                .where(gm.guild.eq(guild))
                .orderBy(
                        new CaseBuilder()
                                .when(gm.guildRole.eq(GuildRole.LEADER)).then(0)
                                .when(gm.guildRole.eq(GuildRole.MANAGER)).then(1)
                                .otherwise(2)
                                .asc(),
                        gm.createdAt.desc()
                )
                .limit(limit)
                .distinct()
                .fetch();
    }
}
