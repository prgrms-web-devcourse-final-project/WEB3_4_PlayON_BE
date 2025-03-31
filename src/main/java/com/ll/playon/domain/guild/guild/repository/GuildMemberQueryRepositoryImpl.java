package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.entity.QGuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GuildMemberQueryRepositoryImpl implements GuildMemberQueryRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<GuildMember> findByGuildOrderByRoleAndCreatedAt(Guild guild, Pageable pageable) {
        QGuildMember gm = QGuildMember.guildMember;

        List<GuildMember> content = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(gm.count())
                .from(gm)
                .where(gm.guild.eq(guild))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
