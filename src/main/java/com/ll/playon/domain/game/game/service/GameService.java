package com.ll.playon.domain.game.game.service;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.steamAPI.SteamAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final SteamAPI steamAPI;
    private final MemberSteamDataRepository memberSteamDataRepository;
    private final MemberRepository memberRepository;

    public List<GameListResponse> makeGameListWithGenre(List<SteamGame> gameList, SteamGenre preferredGenre) {
        return makeGameList(gameList, preferredGenre);
    }

    public List<GameListResponse> makeGameListWithoutGenre(List<SteamGame> gameList) {
        return makeGameList(gameList, null);
    }

    public List<GameListResponse> makeGameList(List<SteamGame> gameList, SteamGenre preferredGenre) {
        final List<GameListResponse> responses = new ArrayList<>();

        for (SteamGame game : gameList) {
            List<String> genres = game.getGenres().stream()
                    .map(SteamGenre::getName)
                    .toList();

            if(preferredGenre != null && !genres.contains(preferredGenre.getName())) continue;

            responses.add(GameListResponse.builder()
                    .appid(game.getAppid())
                    .name(game.getName())
                    .headerImage(game.getHeaderImage())
                    .gameGenres(genres)
                    .build());
        }
        return responses;
    }

    // 메인 페이지에 보여줄 스팀 랭킹
    public List<GameListResponse>  getGameRanking() {
        final List<SteamGame> gameList = gameRepository.findTop5ByAppidIn(steamAPI.getSteamRanking()); // DB 에 없는 게임은 제외됨
        return makeGameListWithoutGenre(gameList);
    }

    // 메인 페이지에 보여줄 사용자 게임 추천
    public List<GameListResponse> getGameRecommendations(Member actor) {
        Member member = memberRepository.findById(actor.getId())
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        // 사용자가 소유하지 않은 게임 필터링
        final List<Long> ownedGames = memberSteamDataRepository.findAllByMemberId(member.getId()).stream()
                .map(MemberSteamData::getAppId).toList();

        final List<Long> notOwnedGames = steamAPI.getSteamRanking().stream()
                .filter(appId -> !ownedGames.contains(appId)).toList();

        // 장르 필터링 후 리스트 완성
        return makeGameListWithGenre(gameRepository.findAllByAppidIn(notOwnedGames), member.getPreferredGenre());
    }
}
