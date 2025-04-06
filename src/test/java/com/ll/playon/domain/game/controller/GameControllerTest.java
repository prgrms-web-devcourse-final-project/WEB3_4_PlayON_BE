package com.ll.playon.domain.game.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ll.playon.domain.game.game.controller.GameController;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;
import com.ll.playon.domain.party.partyLog.repository.PartyLogRepository;
import com.ll.playon.global.openFeign.SteamStoreClient;
import com.ll.playon.global.openFeign.dto.GameItem;
import com.ll.playon.global.openFeign.dto.SteamSearchResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class GameControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private GameRepository gameRepository;
    @Autowired private PartyRepository partyRepository;
    @Autowired private PartyLogRepository partyLogRepository;
    @Autowired private GuildRepository guildRepository;
    @Autowired private TestMemberHelper testMemberHelper;

    @MockitoBean
    private SteamStoreClient mockSteamStoreClient;

    private SteamGame game;

    @BeforeEach
    void setup() {
        partyLogRepository.deleteAll();
        partyRepository.deleteAll();
        guildRepository.deleteAll();
        gameRepository.deleteAll();

        game = gameRepository.save(SteamGame.builder()
                .appid(12345L)
                .name("Test Game")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .headerImage("image.jpg")
                .shortDescription("Short desc")
                .aboutTheGame("About the game")
                .requiredAge(19)
                .website("http://test.com")
                .isWindowsSupported(true)
                .isMacSupported(true)
                .isLinuxSupported(false)
                .isSinglePlayer(true)
                .isMultiPlayer(true)
                .developers("Dev")
                .publishers("Pub")
                .build());
    }

    private void setFakeRakingResponse() {
        SteamSearchResponse fakeResponse = new SteamSearchResponse();
        GameItem game1 = new GameItem();
        GameItem game2 = new GameItem();

        game1.setName("Counter-Strike 2");
        game1.setLogo("https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/730/header.jpg");

        game2.setName("Dota 2");
        game2.setLogo("https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/570/header.jpg");

        fakeResponse.setDesc("");
        fakeResponse.setItems(List.of(game1, game2));

        Mockito.when(mockSteamStoreClient.getGameRanking()).thenReturn(fakeResponse);
    }

    @Test
    @DisplayName("스팀 인기게임 조회")
    void steamRanking() throws Exception {
        setFakeRakingResponse();

        ResultActions resultActions = mvc
                .perform(get("/api/games/ranking"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(GameController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("사용자 게임 추천")
    void noSteamSignup() throws Exception {
        String authMember = "sampleUser1";
        setFakeRakingResponse();

        ResultActions resultActions =
                testMemberHelper.requestWithUserAuth(authMember, get("/api/games/recommend"));

        resultActions
                .andExpect(handler().handlerType(GameController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("게임 상세 정보 조회 성공")
    void getGameDetailSuccess() throws Exception {
        mvc.perform(get("/api/games/{appid}/details", game.getAppid())
                        .param("partyPage.page", "0")
                        .param("partyPage.size", "3")
                        .param("logPage.page", "0")
                        .param("logPage.size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.game.appid").value(game.getAppid()))
                .andExpect(jsonPath("$.data.game.name").value(game.getName()))
                .andExpect(jsonPath("$.data.partyList").isArray())
                .andExpect(jsonPath("$.data.partyLogList").isArray());
    }

    @Test
    @DisplayName("존재하지 않는 게임 조회 실패")
    void getGameDetailFailWhenGameNotFound() throws Exception {
        mvc.perform(get("/api/games/{appid}/details", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게임 목록 조회 성공")
    void getGameListSuccess() throws Exception {
        mvc.perform(get("/api/games/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].appid").value(game.getAppid()));
    }

    @Test
    @DisplayName("게임 자동완성 검색 성공")
    void autoCompleteSuccess() throws Exception {
        mvc.perform(get("/api/games/search")
                        .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Test Game"));
    }

    @Test
    @DisplayName("게임별 파티 목록 조회 성공")
    void getGamePartiesSuccess() throws Exception {
        Party party = partyRepository.save(Party.builder()
                .game(game.getId())
                .name("Test Party")
                .partyAt(LocalDateTime.now().plusDays(1))
                .publicFlag(true)
                .minimum(1)
                .maximum(5)
                .build());

        mvc.perform(get("/api/games/{appid}/party", game.getAppid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].name").value("Test Party"));
    }

    @Test
    @DisplayName("게임별 파티 목록 조회 실패 - 존재하지 않는 게임 ID")
    void getGamePartiesFailWhenGameNotFound() throws Exception {
        mvc.perform(get("/api/games/{appid}/party", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게임별 파티 로그 조회 성공")
    void getGamePartyLogsSuccess() throws Exception {
        Party party = partyRepository.save(Party.builder()
                .game(game.getId())
                .name("Logged Party")
                .partyAt(LocalDateTime.now().minusDays(1))
                .publicFlag(true)
                .minimum(1)
                .maximum(4)
                .build());

        PartyMember member = PartyMember.builder()
                .party(party)
                .partyRole(PartyRole.OWNER)
                .mvpPoint(0)
                .build();

        party.getPartyMembers().add(member);

        PartyLog log = partyLogRepository.save(PartyLog.builder()
                .partyMember(member)
                .comment("Good game")
                .content("Enjoyed it")
                .build());

        mvc.perform(get("/api/games/{appid}/logs", game.getAppid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].partyId").value(party.getId()));
    }

    @Test
    @DisplayName("게임별 파티 로그 조회 실패 - 존재하지 않는 게임 ID")
    void getGamePartyLogsFailWhenGameNotFound() throws Exception {
        mvc.perform(get("/api/games/{appid}/logs", 99999))
                .andExpect(status().isNotFound());
    }
}
