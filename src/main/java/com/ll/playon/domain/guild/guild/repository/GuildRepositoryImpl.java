package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.dto.request.GetGuildListRequest;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.QGuild;
import com.ll.playon.domain.guild.guild.entity.QGuildTag;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GuildRepositoryImpl implements GuildRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Guild> searchGuilds(GetGuildListRequest req, Pageable pageable) {
        QGuild guild = QGuild.guild;
        QGuildTag guildTag = QGuildTag.guildTag;

        BooleanBuilder builder = new BooleanBuilder()
                .and(guild.isDeleted.isFalse())  // 삭제된 길드 제외
                .and(guild.isPublic.isTrue());   // 비공개 길드 제외

        // 이름 검색
        if (req.name() != null && !req.name().isBlank()) {
            builder.and(guild.name.containsIgnoreCase(req.name()));
        }

        // 게임 필터
        if (req.gameIds() != null && !req.gameIds().isEmpty()) {
            builder.and(guild.game.in(req.gameIds()));
        }

        // 태그 필터
        if (req.tagFilters() != null && !req.tagFilters().isEmpty()) {
            for (Map.Entry<String, List<String>> entry : req.tagFilters().entrySet()) {
                String typeKey = entry.getKey();
                List<String> values = entry.getValue();

                // ALL 해당 태그 무시
                if (values.contains("ALL")) continue;

                TagType tagType = TagType.valueOf(typeKey);

                List<TagValue> tagValues = values.stream()
                        .map(TagValue::valueOf)
                        .toList();

                BooleanBuilder tagCondition = new BooleanBuilder();
                for (TagValue tagValue : tagValues) {
                    tagCondition.or(guildTag.type.eq(tagType).and(guildTag.value.eq(tagValue)));
                }

                builder.and(tagCondition);
            }
        }

        List<Guild> content = queryFactory
                .selectDistinct(guild)
                .from(guild)
                .leftJoin(guild.guildTags, guildTag)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(req.sort(), guild))
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                    .select(guild.id.countDistinct())
                    .from(guild)
                    .leftJoin(guild.guildTags, guildTag)
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
