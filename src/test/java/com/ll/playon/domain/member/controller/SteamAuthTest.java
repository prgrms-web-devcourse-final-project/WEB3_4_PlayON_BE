package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class SteamAuthTest {

    @Autowired
    private MockMvc mvc; // 컨트롤러 호출을 위한 MockMvc

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Autowired
    private MemberSteamDataRepository memberSteamDataRepository;

    @Value("${custom.steam.apikey}")
    private String apikey;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("스팀 로그인 성공 테스트, 스팀 id 123, sampleUser1")
    void test1() throws Exception {
        // 가짜 API 응답 설정
        mockServer.expect(requestTo("https://steamcommunity.com/openid/login"))
                .andRespond(withSuccess("is_valid:true", MediaType.TEXT_PLAIN));

        // 컨트롤러 호출
        ResultActions resultActions = mvc.perform(
                get("/api/auth/steam/callback/login")
                .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/123"))))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        // 응답 상태 코드 검증
        resultActions.andExpect(status().isOk());

        // 로그인 후 쿠키 검증
        resultActions.andExpect(
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

        // mock 서버 검증 (API가 제대로 호출되었는지 확인)
        mockServer.verify();
    }

    @Test
    @DisplayName("스팀 회원가입 성공 테스트, 스팀 id 1234, 새로운 사용자")
    void signupNewUser() throws Exception {
        // 가짜 API 응답 설정
        mockServer.expect(requestTo("https://steamcommunity.com/openid/login"))
                .andRespond(withSuccess("is_valid:true", MediaType.TEXT_PLAIN));

        // 가짜 사용자 프로필 응답 설정
        String fakeUserProfileResponse = """
        {
          "response": {
            "players": [
              {
                "personaname": "everydayplayday",
                "avatarfull": "https://avatars.steamstatic.com/66acac8c7e55dd6a4c70dc5eb1d783c015a1d284_full.jpg"
              }
            ]
          }
        }
        """;
        mockServer.expect(requestTo(String.format("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=1234", apikey)))
                .andRespond(withSuccess(fakeUserProfileResponse, MediaType.APPLICATION_JSON));

        // 가짜 게임 리스트 응답 설정
        String fakeGameListResponse = "{ \"response\": { \"games\": [ { \"appid\": 123 }, { \"appid\": 456 } ] } }";
        mockServer.expect(requestTo(String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=1234",apikey)))
                .andRespond(withSuccess(fakeGameListResponse, MediaType.APPLICATION_JSON));


        // 컨트롤러 호출
        ResultActions resultActions = mvc.perform(
                get("/api/auth/steam/callback/signup")
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/1234"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        // 응답 상태 코드 검증
        resultActions.andExpect(status().isOk());

        // 로그인 후 쿠키 검증
        resultActions.andExpect(
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

        // mock 서버 검증 (API가 제대로 호출되었는지 확인)
        mockServer.verify();
    }

    @Test
    @DisplayName("스팀 회원가입 실패 테스트, 스팀 id 123, sampleUser1")
    void test2() throws Exception {
        // 가짜 API 응답 설정
        mockServer.expect(requestTo("https://steamcommunity.com/openid/login"))
                .andRespond(withSuccess("is_valid:true", MediaType.TEXT_PLAIN));

        // 컨트롤러 호출
        ResultActions resultActions = mvc.perform(
                get("/api/auth/steam/callback/signup")
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/123"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        // 응답 상태 코드 검증
        resultActions.andExpect(status().isConflict());

        // mock 서버 검증 (API 가 제대로 호출되었는지 확인)
        mockServer.verify();
    }

    @Test
    @DisplayName("스팀 로그인 실패 테스트, 스팀 id 1234, 새로운 사용자")
    void loginNewUser() throws Exception {
        // 가짜 API 응답 설정
        mockServer.expect(requestTo("https://steamcommunity.com/openid/login"))
                .andRespond(withSuccess("is_valid:true", MediaType.TEXT_PLAIN));

        // 컨트롤러 호출
        ResultActions resultActions = mvc.perform(
                get("/api/auth/steam/callback/login")
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/1234"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        // 응답 상태 코드 검증
        resultActions.andExpect(status().isNotFound());

        // mock 서버 검증 (API 가 제대로 호출되었는지 확인)
        mockServer.verify();
    }

    @Test
    @DisplayName("스팀 계정 연동 성공 테스트, noSteamMember")
    void linkNoSteamMember() throws Exception {
        long initCount = memberSteamDataRepository.count();
        // 가짜 API 응답 설정
        mockServer.expect(requestTo("https://steamcommunity.com/openid/login"))
                .andRespond(withSuccess("is_valid:true", MediaType.TEXT_PLAIN));

        // 가짜 게임 리스트 응답 설정
        String fakeGameListResponse = "{ \"response\": { \"games\": [ { \"appid\": 123 }, { \"appid\": 456 } ] } }";
        mockServer.expect(requestTo(String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=1234",apikey)))
                .andRespond(withSuccess(fakeGameListResponse, MediaType.APPLICATION_JSON));

        // 컨트롤러 호출
        MockHttpServletRequestBuilder request = get("/api/auth/steam/callback/link")
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.mode", List.of("id_res"))))
                        .params(new LinkedMultiValueMap<>(Collections.singletonMap("openid.claimed_id", List.of("https://steamcommunity.com/openid/id/1234"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("noSteamMember", request);

        // 응답 상태 코드 검증
        resultActions.andExpect(status().isOk());

        // count가 3 증가했는지 검증
        long finalCount = memberSteamDataRepository.count();
        assertEquals(initCount + 2, finalCount);

        // mock 서버 검증 (API가 제대로 호출되었는지 확인)
        mockServer.verify();
    }
}

