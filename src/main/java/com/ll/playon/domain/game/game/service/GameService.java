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

    private static final int TOP_FIVE = 5;

    public List<GameListResponse> makeGameListWithGenre(List<Long> appIds, SteamGenre preferredGenre) {
        return makeGameList(appIds, preferredGenre);
    }

    public List<GameListResponse> makeGameListWithoutGenre(List<Long> appIds) {
        return makeGameList(appIds, null);
    }

    public List<GameListResponse> makeGameList(List<Long> appIds, SteamGenre preferredGenre) {
        final List<GameListResponse> responses = new ArrayList<>();

        final List<SteamGame> gameList = gameRepository.findAllByAppidIn(appIds);
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
        // 게임 리스트 id 불러오기
        final List<Long> steamRankingIds = steamAPI.getSteamRanking();

        // 리스트에 없는 게임 DB 추가
        updateGameDB(steamRankingIds);

        // 장르 추가 후 응답
        return makeGameListWithoutGenre(steamRankingIds.stream().limit(TOP_FIVE).toList());
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
        return makeGameListWithGenre(notOwnedGames, member.getPreferredGenre());
    }

    // 인기게임 중에 DB에 없는 게임 추가
    private void updateGameDB(List<Long> appIds) {
        // DB에 없는 게임 찾기

        // 해당 게임의 상세정보 조회

        // DB에 저장
    }
}
