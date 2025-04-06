package com.ll.playon.domain.game.controller;

import com.ll.playon.domain.game.game.controller.GameController;
import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.global.openFeign.SteamStoreClient;
import com.ll.playon.global.openFeign.dto.GameItem;
import com.ll.playon.global.openFeign.dto.SteamSearchResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @MockitoBean
    private SteamStoreClient mockSteamStoreClient;

    private void setFakeRakingResponse() {
        SteamSearchResponse fakeSteamRankingResponse = new SteamSearchResponse();
        GameItem gameItem1 = new GameItem();
        GameItem gameItem2 = new GameItem();

        gameItem1.setName("Counter-Strike 2");
        gameItem1.setLogo("https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/730/header.jpg");

        gameItem2.setName("Dota 2");
        gameItem2.setLogo("https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/570/header.jpg");

        fakeSteamRankingResponse.setDesc("");
        fakeSteamRankingResponse.setItems(List.of(gameItem1, gameItem2));


        Mockito.when(mockSteamStoreClient.getGameRanking())
                .thenReturn(fakeSteamRankingResponse);
    }

    @Test
    @DisplayName("스팀 인기게임 조회")
    void steamRanking() throws Exception {
        setFakeRakingResponse();

        ResultActions resultActions = mvc
                .perform(
                        get("/api/games/ranking")
                )
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
}
