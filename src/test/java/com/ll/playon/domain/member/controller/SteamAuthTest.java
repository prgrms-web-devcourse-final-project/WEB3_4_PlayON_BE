package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.member.repository.MemberSteamDataRepository;
import com.ll.playon.global.openFeign.SteamApiClient;
import com.ll.playon.global.openFeign.SteamOpenIdClient;
import com.ll.playon.global.openFeign.SteamStoreClient;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
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
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        Mockito.when(mockSteamOpenIdClient.validateSteamId(any()))
                .thenReturn("is_valid:true");
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

        Mockito.verify(mockSteamOpenIdClient).validateSteamId(any());
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

        Mockito.verify(mockSteamOpenIdClient).validateSteamId(any());
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

        Mockito.verify(mockSteamOpenIdClient).validateSteamId(any());
    }
}

