package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.enums.Gender;
import com.ll.playon.domain.member.entity.enums.PlayStyle;
import com.ll.playon.domain.member.entity.enums.SkillLevel;
import com.ll.playon.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Test
    @DisplayName("일반 회원 회원가입")
    void noSteamSignup() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members/signup")
                                .content("""
                                        {
                                            "username": "noSteamTestUser",
                                            "password": "noSteam123"
                                        }
                                        """.stripIndent()
                                )
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        Member actor = memberService.findByUsername("noSteamTestUser").get();


        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.nickname").value(actor.getNickname()));

        resultActions.andExpect(
                result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isEqualTo(actor.getApiKey());
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                }
        );
    }

    @Test
    @DisplayName("일반 회원 로그인")
    void noSteamLogin() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members/login")
                                .content("""
                                    {
                                        "username": "noSteamMember",
                                        "password": "noSteam123"
                                    }
                                    """.stripIndent()
                                )
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        Member actor = memberService.findByUsername("noSteamMember").get();


        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(status().isOk());

        resultActions.andExpect(
                result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isEqualTo(actor.getApiKey());
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                }
        );
    }

    @Test
    @DisplayName("일반회원 회원가입 실패")
    void noSteamSignupFail() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members/signup")
                                .content("""
                                    {
                                        "username": "noSteamMember",
                                        "password": "noSteam123"
                                    }
                                    """.stripIndent()
                                )
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("일반회원 로그인 실패")
    void noSteamLoginFail() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members/login")
                                .content("""
                                    {
                                        "username": "noSteamTestUser",
                                        "password": "noSteam123"
                                    }
                                    """.stripIndent()
                                )
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원 정보 수정")
    void modifyMember() throws Exception {
        String authMember = "sampleUser1";

        MockHttpServletRequestBuilder request = put("/api/members/me")
                .content("""
                        {
                            "nickname": "changedNickname",
                            "profileImg": "123",
                            "playStyle": "NORMAL",
                            "skillLevel": "HACKER",
                            "gender": "FEMALE"
                        }
                        """)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));
        ResultActions resultActions = testMemberHelper.requestWithUserAuth(authMember, request);

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(status().isOk());

        Member memberCheck = memberService.findByUsername(authMember).get();

        assertEquals("changedNickname", memberCheck.getNickname());
        assertEquals(PlayStyle.NORMAL, memberCheck.getPlayStyle());
        assertEquals(SkillLevel.HACKER, memberCheck.getSkillLevel());
        assertEquals(Gender.FEMALE, memberCheck.getGender());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deactivateMember() throws Exception {
        String authMember = "sampleUser1";

        MockHttpServletRequestBuilder request = delete("/api/members/me");
        ResultActions resultActions = testMemberHelper.requestWithUserAuth(authMember, request);

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(status().isOk());

        Optional<Member> memberCheck = memberService.findByUsername(authMember);

        assertTrue(memberCheck.isEmpty());
    }
}