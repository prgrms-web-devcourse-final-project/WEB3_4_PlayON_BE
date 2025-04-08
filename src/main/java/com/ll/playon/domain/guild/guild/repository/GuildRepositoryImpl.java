package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.dto.request.GetGuildListRequest;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.QGuild;
import com.ll.playon.domain.guild.guild.entity.QGuildTag;
import com.ll.playon.domain.guild.guildBoard.entity.QGuildBoard;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
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
    public Page<Guild> searchGuilds(GetGuildListRequest req, Pageable pageable, String sort) {
        QGuild guild = QGuild.guild;
        QGuildTag guildTag = QGuildTag.guildTag;
        QGuildBoard board = QGuildBoard.guildBoard;

        BooleanBuilder builder = new BooleanBuilder()
                .and(guild.isDeleted.isFalse())  // 삭제된 길드 제외
                .and(guild.isPublic.isTrue());   // 비공개 길드 제외

        // 이름 검색
        if (req.getName() != null && !req.getName().isBlank()) {
            builder.and(guild.name.containsIgnoreCase(req.getName()));
        }

        // 게임 필터
        if (req.getAppids() != null && !req.getAppids().isEmpty()) {
            builder.and(guild.game.appid.in(req.getAppids()));
        }

        // 태그 필터
        if (req.getTags() != null && !req.getTags().isEmpty()) {
            for (Map.Entry<String, List<String>> entry : req.getTags().entrySet()) {
                String typeKey = entry.getKey();
                List<String> values = entry.getValue();

                if (values.contains("ALL")) continue;

                TagType tagType = TagType.valueOf(typeKey);
                List<TagValue> tagValues = values.stream()
                        .map(TagValue::valueOf)
                        .toList();

                QGuildTag subTag = new QGuildTag("subTag");

                builder.and(JPAExpressions.selectOne()
                        .from(subTag)
                        .where(subTag.guild.eq(guild)
                                .and(subTag.type.eq(tagType))
                                .and(subTag.value.in(tagValues)))
                        .exists());
            }
        }

// 정렬 분기 처리
        boolean isActivitySort = sort.equals("activity");

        List<Guild> content = queryFactory
                .select(guild)
                .from(guild)
                .leftJoin(guild.guildTags, guildTag)
                .leftJoin(guild.guildBoards, board)
                .where(builder)
                .groupBy(guild.id)
                .orderBy(getSort(sort, guild, board))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
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

    private OrderSpecifier<?> getSort(String sort, QGuild guild, QGuildBoard board) {
        return switch (sort) {
            case "members" -> guild.maxMembers.desc(); // 멤버 많은 순
            case "activity" -> board.id.count().desc(); // 게시글 많은 순
            default -> guild.createdAt.desc(); // 최신순
        };
    }
}
