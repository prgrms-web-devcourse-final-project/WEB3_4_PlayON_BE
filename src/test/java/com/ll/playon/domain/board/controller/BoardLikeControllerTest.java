package com.ll.playon.domain.board.controller;

import com.ll.playon.domain.board.entity.Board;
import com.ll.playon.domain.board.enums.BoardCategory;
import com.ll.playon.domain.board.repository.BoardRepository;
import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class BoardLikeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시글 좋아요 등록 성공")
    void likeBoard_success() throws Exception {
        // given
        Member member = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(member)
                .title("좋아요 테스트 게시글")
                .content("내용")
                .category(BoardCategory.DAILY)
                .build());

        // when
        MockHttpServletRequestBuilder request = post("/api/boards/" + board.getId() + "/like");

        ResultActions resultActions = testMemberHelper.requestWithUserAuth(member.getUsername(), request);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardLikeController.class))
                .andExpect(jsonPath("$.resultCode").value("OK"))
                .andExpect(jsonPath("$.data").value("좋아요 등록 성공"));
    }

    @Test
    @DisplayName("게시글 좋아요 취소 성공")
    void cancelLikeBoard_success() throws Exception {
        // given
        Member member = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(member)
                .title("좋아요 취소 테스트 게시글")
                .content("내용")
                .category(BoardCategory.DAILY)
                .build());

        // 먼저 좋아요 등록 (Service 호출 or Controller 호출 가능)
        mvc.perform(post("/api/boards/" + board.getId() + "/like")
                .header("Authorization", "Bearer " + member.getApiKey() + " asd"));

        // when
        MockHttpServletRequestBuilder request = delete("/api/boards/" + board.getId() + "/like");

        ResultActions resultActions = testMemberHelper.requestWithUserAuth(member.getUsername(), request);

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardLikeController.class))
                .andExpect(jsonPath("$.resultCode").value("OK"))
                .andExpect(jsonPath("$.data").value("좋아요 취소 성공"));
    }
}
