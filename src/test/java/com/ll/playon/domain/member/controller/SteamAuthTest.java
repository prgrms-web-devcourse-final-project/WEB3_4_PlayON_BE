package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.global.openFeign.SteamApiClient;
import com.ll.playon.global.openFeign.SteamOpenIdClient;
import com.ll.playon.global.openFeign.SteamStoreClient;
import com.ll.playon.global.openFeign.dto.ownedGames.Game;
import com.ll.playon.global.openFeign.dto.ownedGames.GameResponse;
import com.ll.playon.global.openFeign.dto.ownedGames.SteamGameResponse;
import com.ll.playon.global.openFeign.dto.playerSummaries.Player;
import com.ll.playon.global.openFeign.dto.playerSummaries.PlayerResponse;
import com.ll.playon.global.openFeign.dto.playerSummaries.SteamResponse;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class SteamAuthTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Autowired
    private MemberSteamDataRepository memberSteamDataRepository;

    @MockitoBean
    private SteamOpenIdClient mockSteamOpenIdClient;

    @MockitoBean
    private SteamApiClient mockSteamApiClient;

    @MockitoBean
    private SteamStoreClient mockSteamStoreClient;

    @Value("${custom.steam.apikey}")
    private String apikey;

    private void setFakeLoginResponse() {
        Mockito.when(mockSteamOpenIdClient.validateSteamId(Mockito.any()))
                .thenReturn("is_valid:true");
    }

    private void setFakeProfileResponse() {
        SteamResponse fakeSteamResponse = new SteamResponse();
        PlayerResponse playerResponse = new PlayerResponse();
        Player player = new Player();

        player.setNickname("everydayplayday");
        player.setAvatar("https://avatars.steamstatic.com/66acac8c7e55dd6a4c70dc5eb1d783c015a1d284_medium.jpg");

        playerResponse.setPlayers(List.of(player));
        fakeSteamResponse.setResponse(playerResponse);

        Mockito.when(mockSteamApiClient.getPlayerSummaries(eq(apikey),Mockito.any()))
                .thenReturn(fakeSteamResponse);
    }

    private void setFakeGamesResponse() {
        SteamGameResponse fakeSteamGameResponse = new SteamGameResponse();
        GameResponse gameResponse = new GameResponse();
        Game game1 = new Game();
        Game game2 = new Game();
        Game game3 = new Game();

        game1.setAppId("2246340");
        game1.setPlaytime(111);
        game2.setAppId("2680010");
        game2.setPlaytime(222);
        game3.setAppId("2456740");
        game3.setPlaytime(333);

        gameResponse.setGames(List.of(game1, game2, game3));
        fakeSteamGameResponse.setResponse(gameResponse);

        Mockito.when(mockSteamApiClient.getPlayerOwnedGames(eq(apikey),Mockito.any()))
                .thenReturn(fakeSteamGameResponse);
    }

    @Test
    @DisplayName("스팀 로그인 성공 테스트, 스팀 id 123, sampleUser1")
    void test1() throws Exception {
        setFakeLoginResponse();

        ResultActions resultActions = mvc.perform(
                get("/api/auth/steam/callback/login")
                .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/123"))))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(
                result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie).isNotNull();
                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie).isNotNull();
                    assertThat(apiKeyCookie.getValue()).isNotBlank();
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                }
        );

        Mockito.verify(mockSteamOpenIdClient).validateSteamId(Mockito.any());
    }

    @Test
    @DisplayName("스팀 회원가입 성공 테스트, 스팀 id 1234, 새로운 사용자")
    void signupNewUser() throws Exception {
        setFakeLoginResponse();
        setFakeProfileResponse();
        setFakeGamesResponse();

        ResultActions resultActions = mvc.perform(
                get("/api/auth/steam/callback/signup")
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/1234"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(
                result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie).isNotNull();
                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie).isNotNull();
                    assertThat(apiKeyCookie.getValue()).isNotBlank();
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                }
        );

        Mockito.verify(mockSteamOpenIdClient).validateSteamId(Mockito.any());
        Mockito.verify(mockSteamApiClient).getPlayerOwnedGames(eq(apikey),Mockito.any());
        Mockito.verify(mockSteamApiClient).getPlayerSummaries(eq(apikey),Mockito.any());
    }

    @Test
    @DisplayName("스팀 회원가입 실패 테스트, 스팀 id 123, sampleUser1")
    void test2() throws Exception {
        setFakeLoginResponse();

        ResultActions resultActions = mvc.perform(
                get("/api/auth/steam/callback/signup")
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/123"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        resultActions.andExpect(status().isConflict());

        Mockito.verify(mockSteamOpenIdClient).validateSteamId(Mockito.any());
    }

    @Test
    @DisplayName("스팀 로그인 실패 테스트, 스팀 id 1234, 새로운 사용자")
    void loginNewUser() throws Exception {
        setFakeLoginResponse();

        ResultActions resultActions = mvc.perform(
                get("/api/auth/steam/callback/login")
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/1234"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        resultActions.andExpect(status().isNotFound());

        Mockito.verify(mockSteamOpenIdClient).validateSteamId(Mockito.any());
    }

    @Test
    @DisplayName("스팀 계정 연동 성공 테스트, noSteamMember")
    void linkNoSteamMember() throws Exception {
        long initCount = memberSteamDataRepository.count();
        setFakeLoginResponse();
        setFakeGamesResponse();

        MockHttpServletRequestBuilder request = get("/api/auth/steam/callback/link")
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/1234"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("noSteamMember", request);

        resultActions.andExpect(status().isOk());

        // count 가 3 증가했는지 검증
        long finalCount = memberSteamDataRepository.count();
        assertEquals(initCount + 3, finalCount);

        Mockito.verify(mockSteamOpenIdClient).validateSteamId(Mockito.any());
        Mockito.verify(mockSteamApiClient).getPlayerOwnedGames(eq(apikey),Mockito.any());
    }
}

