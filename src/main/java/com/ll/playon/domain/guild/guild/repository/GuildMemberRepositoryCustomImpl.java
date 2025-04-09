package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.entity.QGuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
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

        return queryFactory
                .selectFrom(gm)
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
                .fetch();
    }
}
