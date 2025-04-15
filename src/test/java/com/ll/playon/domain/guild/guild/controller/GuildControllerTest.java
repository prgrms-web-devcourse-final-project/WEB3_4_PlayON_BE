package com.ll.playon.domain.guild.guild.controller;

import com.ll.playon.domain.guild.guild.TestGuildHelper;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.member.repository.MemberRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class GuildControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Autowired
    private GuildRepository guildRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GuildMemberRepository guildMemberRepository;
    @Autowired
    private TestGuildHelper testGuildHelper;

    @Test
    @DisplayName("길드 생성 성공")
    void addGuild() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/guilds")
                .content("""
                        {
                            "name": "테스트",
                            "description": "길드 생성 테스트",
                            "maxMembers": 3,
                            "isPublic": true,
                            "appid": 1,
                            "tags": [
                                { "type": "파티 스타일", "value": "하드" },
                                { "type": "게임 실력", "value": "뉴비" },
                                { "type": "성별", "value": "남자만" },
                                { "type": "친목", "value": "친목 환영" }
                            ]
                        }
                        """)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions.andExpect(handler().handlerType(GuildController.class))
                .andExpect(handler().methodName("addGuild"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("CREATED"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data.name").value("테스트"))
                .andExpect(jsonPath("$.data.tags", hasSize(4)));
    }

    @Test
    @DisplayName("길드 생성 실패 - 중복 이름")
    void createGuild_fail_duplicate() throws Exception {
        Guild guild = testGuildHelper.createPublicGuildWithLeader("중복길드", "sampleUser1");

        MockHttpServletRequestBuilder request = post("/api/guilds")
                .content("""
                        {
                            "name": "%s",
                            "description": "소개글",
                            "maxMembers": 10,
                            "isPublic": true,
                            "appId": 730
                        }
                        """.formatted(guild.getName()))
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions.andExpect(handler().handlerType(GuildController.class))
                .andExpect(handler().methodName("addGuild"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("길드 상세 조회")
    void getGuildDetail() throws Exception {
        Guild guild = guildRepository.findWithTagsById(1L).get();

        MockHttpServletRequestBuilder request = get("/api/guilds/" + guild.getId());

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser3", request);

        resultActions
                .andExpect(handler().handlerType(GuildController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(guild.getName()))
                .andExpect(jsonPath("$.data.description").value(guild.getDescription()));
    }

    @Test
    @DisplayName("길드 수정 실패 - 권한 없음")
    void modifyGuild_fail_noPermission() throws Exception {
        Guild guild = testGuildHelper.createPublicGuildWithLeader("수정테스트길드", "sampleUser1");

        MockHttpServletRequestBuilder request = put("/api/guilds/" + guild.getId())
                .content("""
                        {
                            "name": "수정된이름",
                            "description": "수정된소개",
                            "maxMembers": 15,
                            "appId": 730,
                            "isPublic": false,
                            "newFileType": "",
                            "tags": [
                                { "type": "파티 스타일", "value": "하드" },
                                { "type": "게임 실력", "value": "뉴비" },
                                { "type": "성별", "value": "남자만" },
                                { "type": "친목", "value": "친목 환영" }
                            ]
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser2", request);

        resultActions.andExpect(handler().handlerType(GuildController.class))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("길드 삭제 성공")
    void deleteGuild() throws Exception {
        Guild guild = testGuildHelper.createPublicGuildWithLeader("삭제길드", "sampleUser1");

        MockHttpServletRequestBuilder request = delete("/api/guilds/" + guild.getId());

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions.andExpect(handler().handlerType(GuildController.class))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("길드 삭제 실패 - 권한 없음")
    void deleteGuild_fail_noPermission() throws Exception {
        Guild guild = testGuildHelper.createPublicGuildWithLeader("삭제실패길드", "sampleUser1");

        MockHttpServletRequestBuilder request = delete("/api/guilds/" + guild.getId());

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser2", request);

        resultActions.andExpect(handler().handlerType(GuildController.class))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("길드 멤버 조회 성공 - 공개 길드 + 비멤버")
    void getGuildMembers_publicGuild_guest() throws Exception {
        Guild guild = testGuildHelper.createPublicGuildWithLeader("공개길드", "sampleUser1");

        MockHttpServletRequestBuilder request = get("/api/guilds/" + guild.getId() + "/members/page");

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser2", request);

        resultActions.andExpect(handler().handlerType(GuildController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }


    @Test
    @DisplayName("길드 멤버 조회 성공 - 비공개 길드 + 멤버")
    void getGuildMembers_privateGuild_member() throws Exception {
        Guild guild = testGuildHelper.createPrivateGuildWithLeader("비공개길드", "sampleUser1");

        MockHttpServletRequestBuilder request = get("/api/guilds/" + guild.getId() + "/members/page");

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions.andExpect(handler().handlerType(GuildController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }


    @Test
    @DisplayName("길드 멤버 조회 실패 - 비공개 길드 + 비멤버")
    void getGuildMembers_privateGuild_guest() throws Exception {
        Guild guild = testGuildHelper.createPrivateGuildWithLeader("비공개길드", "sampleUser1");

        MockHttpServletRequestBuilder request = get("/api/guilds/" + guild.getId() + "/members/page");

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser2", request);

        resultActions.andExpect(handler().handlerType(GuildController.class))
                .andExpect(status().isForbidden());
    }

}
