package com.ll.playon.domain.member.service;

import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.service.TitleEvaluator;
import com.ll.playon.global.steamAPI.SteamAPI;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SteamAsyncService {

    private final SteamAPI steamAPI;
    private final MemberRepository memberRepository;
    private final TitleEvaluator titleEvaluator;
    private final MemberSteamDataRepository memberSteamDataRepository;

    @Async
    @Transactional
    public void getUserGamesAndCheckGenres(Member member) {
        int before = member.getGames().size();

        List<Long> userGames = steamAPI.getUserGames(member.getSteamId());
        if(!userGames.isEmpty()) {
            SteamGenre preferredGenre = steamAPI.getPreferredGenre(userGames);
            memberRepository.save(member.toBuilder().preferredGenre(preferredGenre.getName()).build());
            saveUserGameList(userGames, member);
        }

        int after = userGames.size();

        // 스팀 게임 소유 칭호
        titleEvaluator.gameCountCheck(ConditionType.STEAM_GAME_COUNT, member, after - before);
    }

    public void saveUserGameList(List<Long> gameList, Member member) {
        Set<Long> existingGames = member.getGames().stream()
                .map(MemberSteamData::getAppId)
                .collect(Collectors.toSet());

        List<MemberSteamData> games = gameList.stream()
                .filter(appid -> !existingGames.contains(appid))
                .map(appId -> MemberSteamData.builder()
                        .appId(appId)
                        .member(member)
                        .build())
                .toList();

        member.getGames().addAll(games);
        memberSteamDataRepository.saveAll(games);
    }
}
