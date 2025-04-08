package com.ll.playon.domain.guild.guild.controller;

import com.ll.playon.domain.member.TestMemberHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class GuildControllerTest {

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Test
    @DisplayName("길드 생성")
    void addGuild() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/guilds")
                .content("""
                        {
                            "name": "테스트",
                            "description": "길드 생성 테스트",
                            "maxMembers": 3,
                            "isPublic": true,
                            "appid": 1,
                            "guildImg": "a.png",
                            "tags": [
                                {
                                    "type": "파티 스타일",
                                    "value": "빡겜"
                                },
                                {
                                    "type": "게임 실력",
                                    "value": "뉴비"
                                },
                                {
                                    "type": "성별",
                                    "value": "남자만"
                                },
                                {
                                    "type": "친목",
                                    "value": "친목 환영"
                                }
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
                .andExpect(jsonPath("$.data.guildImg").value("a.png"))
                .andExpect(jsonPath("$.data.tags", hasSize(4)));
    }
}
