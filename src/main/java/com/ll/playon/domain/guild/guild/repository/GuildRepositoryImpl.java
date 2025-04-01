package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.dto.GetGuildListRequest;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.QGuild;
import com.ll.playon.domain.guild.guild.enums.ActiveTime;
import com.ll.playon.domain.guild.guild.enums.GameSkill;
import com.ll.playon.domain.guild.guild.enums.GenderFilter;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GuildRepositoryImpl implements GuildRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Guild> searchGuilds(GetGuildListRequest req, Pageable pageable) {
        QGuild guild = QGuild.guild;

        BooleanBuilder builder = new BooleanBuilder()
                .and(guild.isDeleted.isFalse())
                .and(guild.isPublic.isTrue());

        if (req.name() != null && !req.name().isBlank()) {
            builder.and(guild.name.containsIgnoreCase(req.name()));
        }

        if (req.gameIds() != null && !req.gameIds().isEmpty()) {
            builder.and(guild.game.in(req.gameIds()));
        }

        if (req.partyStyles() != null && !req.partyStyles().isEmpty()) {
            builder.and(guild.partyStyle.in(req.partyStyles()));
        }

        if (req.gameSkills() != null && !req.gameSkills().isEmpty()) {
            if (!req.gameSkills().contains(GameSkill.ALL)) {
                builder.and(guild.gameSkill.in(req.gameSkills()));
            }
        }

        if (req.genderFilters() != null && !req.genderFilters().isEmpty()) {
            if (!req.genderFilters().contains(GenderFilter.ALL)) {
                builder.and(guild.genderFilter.in(req.genderFilters()));
            }
        }

        if (req.activeTimes() != null && !req.activeTimes().isEmpty()) {
            if (!req.activeTimes().contains(ActiveTime.ALL)) {
                builder.and(guild.activeTime.in(req.activeTimes()));
            }
        }

        List<Guild> content = queryFactory
                .selectFrom(guild)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(req.sort(), guild))
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                .select(guild.count())
                .from(guild)
                .where(builder)
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?>[] getSort(String sort, QGuild guild) {
        return switch (sort) {
            // TODO: activity 활동 많은 순 구현 필요(일주일당 게시글 많은 순)
            case "members" -> new OrderSpecifier[]{guild.maxMembers.desc()};
            default -> new OrderSpecifier[]{guild.createdAt.desc()}; // 최신순 기본
        };
    }
}
