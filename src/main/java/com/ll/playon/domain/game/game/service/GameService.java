package com.ll.playon.domain.game.game.service;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.dto.request.GameSearchCondition;
import com.ll.playon.domain.game.game.dto.response.*;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.game.game.repository.LongPlaytimeGameRepository;
import com.ll.playon.domain.game.game.repository.WeeklyGameRepository;
import com.ll.playon.domain.image.repository.ImageRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.MemberSteamData;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import com.ll.playon.domain.party.party.dto.PartyDetailTagDto;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.repository.PartyMemberRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.domain.party.party.type.PartyStatus;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;
import com.ll.playon.domain.party.partyLog.repository.PartyLogRepository;
import com.ll.playon.domain.title.service.MemberTitleService;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.steamAPI.SteamAPI;
import com.ll.playon.standard.page.dto.PageDto;
import com.ll.playon.standard.time.dto.TotalPlayTimeDto;
import com.ll.playon.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final SteamAPI steamAPI;
    private final MemberSteamDataRepository memberSteamDataRepository;
    private final MemberRepository memberRepository;
    private final PartyRepository partyRepository;
    private final PartyLogRepository partyLogRepository;
    private final MemberTitleService memberTitleService;

    private final static int TOP_FIVE = 5;
    private final WeeklyGameRepository weeklyGameRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final LongPlaytimeGameRepository longPlaytimeGameRepository;

    public List<GameListResponse> makeGameListWithGenre(List<SteamGame> gameList, String preferredGenre) {
        return makeGameList(gameList, preferredGenre);
    }

    public List<GameListResponse> makeGameListWithoutGenre(List<SteamGame> gameList) {
        return makeGameList(gameList, null);
    }

    public List<GameListResponse> makeGameList(List<SteamGame> gameList, String preferredGenre) {
        final List<GameListResponse> responses = new ArrayList<>();

        for (SteamGame game : gameList) {
            List<String> genres = game.getGenres().stream()
                    .map(SteamGenre::getName)
                    .toList();

            if(preferredGenre != null && !genres.contains(preferredGenre)) continue;

            responses.add(GameListResponse.builder()
                    .appid(game.getAppid())
                    .name(game.getName())
                    .headerImage(game.getHeaderImage())
                    .genres(genres)
                    .build());
        }
        return responses;
    }

    // 메인 페이지에 보여줄 스팀 랭킹
    @Transactional
    public List<GameListResponse>  getGameRanking() {
        return makeGameListWithoutGenre(steamAPI.getSteamRanking().stream().limit(TOP_FIVE).toList());
    }

    // 메인 페이지에 보여줄 사용자 게임 추천
    @Transactional(readOnly = true)
    public List<GameListResponse> getGameRecommendations(Member actor) {
        Member member = memberRepository.findById(actor.getId())
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        // 사용자가 소유하지 않은 게임 필터링
        final List<Long> ownedGames = memberSteamDataRepository.findAllByMemberId(member.getId()).stream()
                .map(MemberSteamData::getAppId).toList();

        final List<SteamGame> notOwnedGames = steamAPI.getSteamRanking().stream()
                .filter(steamGame -> !ownedGames.contains(steamGame.getAppid())).toList();

        // 장르 필터링 후 리스트 완성
        return makeGameListWithGenre(notOwnedGames, member.getPreferredGenre());
    }

    public GameDetailWithPartyResponse getGameDetailWithParties(Long appid) {
        Pageable partyPageable = PageRequest.of(0, 3);

        SteamGame game = gameRepository.findSteamGameByAppid(appid)
                .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);

        Page<Party> partyPage = partyRepository.findByGame(game, partyPageable);

        // 완료된 파티 조회
        List<Party> completedParties = partyRepository.findRecentCompletedPartiesWithLogs(
                        PartyStatus.COMPLETED, Pageable.unpaged()
                ).stream()
                .filter(p -> p.getGame().getAppid().equals(appid))
                .toList();

        Map<Long, PartyMember> mvpMap = new HashMap<>();
        Map<Long, TotalPlayTimeDto> playTimeMap = new HashMap<>();
        Map<Long, List<PartyDetailMemberDto>> memberMap = new HashMap<>();
        Map<Long, List<PartyDetailTagDto>> tagMap = new HashMap<>();

        for (Party party : completedParties) {
            List<PartyMember> members = party.getPartyMembers().stream()
                    .filter(pm -> pm.getPartyRole().equals(PartyRole.MEMBER) || pm.getPartyRole().equals(PartyRole.OWNER))
                    .toList();

            PartyMember mvp = members.stream()
                    .filter(pm -> pm.getMvpPoint() > 0)
                    .max(Comparator.comparingInt(PartyMember::getMvpPoint))
                    .orElse(members.stream()
                            .filter(pm -> pm.getPartyRole().equals(PartyRole.OWNER))
                            .findFirst()
                            .orElse(null));

            TotalPlayTimeDto playTime = Ut.Time.getTotalPlayTime(party.getPartyAt(), party.getEndedAt());

            Map<Long, String> titleMap = memberTitleService.getRepresentativeTitleMap(
                    members.stream().map(pm -> pm.getMember().getId()).toList()
            );

            List<PartyDetailMemberDto> memberDtos = members.stream()
                    .map(pm -> new PartyDetailMemberDto(pm, titleMap))
                    .toList();

            List<PartyDetailTagDto> tagDtos = party.getPartyTags().stream()
                    .map(PartyDetailTagDto::new)
                    .toList();

            mvpMap.put(party.getId(), mvp);
            playTimeMap.put(party.getId(), playTime);
            memberMap.put(party.getId(), memberDtos);
            tagMap.put(party.getId(), tagDtos);
        }

        return GameDetailWithPartyResponse.from(
                game,
                partyPage.getContent(),
                completedParties,
                mvpMap,
                playTimeMap,
                memberMap,
                tagMap
        );
    }

    @Transactional(readOnly = true)
    public PageDto<GameSummaryResponse> searchGames(GameSearchCondition condition, Pageable pageable) {
        Page<SteamGame> result = gameRepository.searchGames(condition, pageable);
        return new PageDto<>(result.map(GameSummaryResponse::from));
    }

    @Transactional(readOnly = true)
    public List<GameAutoCompleteResponse> autoCompleteGames(String keyword) {
        return gameRepository.searchByGameName(keyword)
                .stream()
                .map(GameAutoCompleteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageDto<PartySummaryResponse> getGameParties(Long appid, Pageable pageable) {
        SteamGame game = gameRepository.findSteamGameByAppid(appid)
                .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);

        Page<Party> page = partyRepository.findByGame(game, pageable);
        return new PageDto<>(page.map(PartySummaryResponse::from));
    }

    @Transactional(readOnly = true)
    public PageDto<PartyLogSummaryResponse> getGamePartyLogs(Long appid, Pageable pageable) {
        SteamGame game = gameRepository.findSteamGameByAppid(appid)
                .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);

        Page<PartyLog> page = partyLogRepository.findByPartyGame(game, pageable);
        return new PageDto<>(page.map(PartyLogSummaryResponse::from));
    }

    @Transactional(readOnly = true)
    public List<GetWeeklyPopularGameResponse> getWeeklyPopularGames(LocalDate week) {
        List<Long> gameIds = weeklyGameRepository.findTopGameIdsByWeek(week);
        List<SteamGame> games = gameRepository.findAllByAppidIn(gameIds);
        Map<Long, SteamGame> gameMap = games.stream()
                .collect(Collectors.toMap(SteamGame::getAppid, g -> g));

        return gameIds.stream()
                .map(gameMap::get)
                .map(GetWeeklyPopularGameResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetRecommendedGameResponse> recommendGamesForMember(Long myMemberId) {
        int limit = 4;

        // 내가 참여한 파티들
        List<Long> myPartyIds = partyMemberRepository.findPartyIdsByMemberId(myMemberId);
        if (myPartyIds.isEmpty()) {
            return List.of();
        }

        // 그 파티에서 같이한 멤버
        List<Long> friendMemberIds = partyMemberRepository.findMemberIdsInPartiesExceptMe(myPartyIds, myMemberId);
        if (friendMemberIds.isEmpty()) {
            return List.of();
        }

        // 그 멤버들이 참여한 파티 중 내가 참여한 파티 제외
        List<Long> friendPartyIds = partyMemberRepository.findPartyIdsByMembersExceptPartyIds(friendMemberIds, myPartyIds);
        if (friendPartyIds.isEmpty()) {
            return List.of();
        }

        // 공개 + 완료 파티를 최신순으로 limit 만큼
        List<Party> parties = partyRepository.findPublicCompletedPartiesIn(friendPartyIds, PageRequest.of(0, limit*4));
        if (parties.isEmpty()) {
            return List.of();
        }

        // TODO: distinct() -> equals() 구현 필요
        return parties.stream()
                .map(Party::getGame)
                .collect(Collectors.toMap(
                        SteamGame::getAppid,
                        GetRecommendedGameResponse::from,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .limit(limit)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetRecommendedGameResponse> getTopPlaytimeGames(LocalDate week) {
        List<Long> appIds = longPlaytimeGameRepository.findAppIdsByWeek(week);
        List<SteamGame> games = gameRepository.findAllByIdIn(appIds);
        Map<Long, SteamGame> gameMap = games.stream()
                .collect(Collectors.toMap(SteamGame::getId, g -> g));

        return appIds.stream()
                .map(gameMap::get)
                .map(GetRecommendedGameResponse::from)
                .toList();
    }
}
