package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.QSteamGame;
import com.ll.playon.domain.game.game.dto.request.GameSearchCondition;
import com.ll.playon.domain.game.game.entity.*;
import com.ll.playon.domain.game.game.enums.PlayerType;
import com.ll.playon.domain.game.game.enums.ReleaseStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SteamGame> searchByGameName(String keyword) {
        QSteamGame game = QSteamGame.steamGame;

        return queryFactory
                .selectFrom(game)
                .where(game.name.containsIgnoreCase(keyword))
                .limit(5)
                .fetch();
    }

    @Override
    public Page<SteamGame> searchGames(GameSearchCondition condition, Pageable pageable) {
        QSteamGame game = QSteamGame.steamGame;
        QSteamGenre genre = QSteamGenre.steamGenre;

        JPQLQuery<SteamGame> query = queryFactory
                .selectFrom(game)
                .leftJoin(game.genres, genre).fetchJoin()
                .distinct()
                .where(
                        keywordContains(condition.keyword()),
                        isMac(condition.isMacSupported()),
                        releasedAfter(condition.releasedAfter()),
                        releaseStatusEq(condition.releaseStatus()),
                        genreIn(condition.genres()),
                        playerTypeEq(condition.playerType())
                );

        long total = query.fetch().size();
        List<SteamGame> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression keywordContains(String keyword) {
        return keyword != null && !keyword.isBlank()
                ? QSteamGame.steamGame.name.containsIgnoreCase(keyword)
                : null;
    }

    private BooleanExpression isMac(Boolean isMac) {
        return isMac != null
                ? QSteamGame.steamGame.isMacSupported.eq(isMac)
                : null;
    }

    private BooleanExpression releasedAfter(LocalDate releasedAfter) {
        return releasedAfter != null
                ? QSteamGame.steamGame.releaseDate.goe(releasedAfter)
                : null;
    }

    private BooleanExpression releaseStatusEq(ReleaseStatus status) {
        if (status == null) return null;
        return switch (status) {
            case RELEASED -> QSteamGame.steamGame.releaseDate.loe(LocalDate.now());
            case UNRELEASED -> QSteamGame.steamGame.releaseDate.gt(LocalDate.now());
        };
    }

    private BooleanExpression genreIn(List<String> genres) {
        return genres != null && !genres.isEmpty()
                ? QSteamGenre.steamGenre.name.in(genres)
                : null;
    }

    private BooleanExpression playerTypeEq(PlayerType type) {
        if (type == null) return null;

        return switch (type) {
            case SINGLE -> QSteamGame.steamGame.isSinglePlayer.isTrue();
            case MULTI -> QSteamGame.steamGame.isMultiPlayer.isTrue();
        };
    }
}