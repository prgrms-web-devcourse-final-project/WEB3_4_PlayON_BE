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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("게시글 작성 성공")
    void createBoard_success() throws Exception {
        String content = """
                {
                    "title": "첫 게시글",
                    "content": "게시판 테스트 작성입니다.",
                    "category": "DAILY",
                    "fileType": ""
                }
                """;

        MockHttpServletRequestBuilder request = post("/api/boards")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 상세 조회 성공")
    void getBoardDetail_success() throws Exception {
        Member writer = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(writer)
                .title("상세조회글")
                .content("상세내용")
                .category(BoardCategory.DAILY)
                .build()
        );

        MockHttpServletRequestBuilder request = get("/api/boards/" + board.getId());

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser2", request);

        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.boardTitle").value("상세조회글"))
                .andExpect(jsonPath("$.data.content").value("상세내용"));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateBoard_success() throws Exception {
        Member writer = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(writer)
                .title("수정전 제목")
                .content("수정전 내용")
                .category(BoardCategory.GAME_NEWS)
                .build()
        );

        MockHttpServletRequestBuilder request = put("/api/boards/" + board.getId())
                .content("""
                        {
                            "title": "수정된 제목",
                            "content": "수정된 내용",
                            "category": "DAILY",
                            "newFileType": "",
                            "deleteUrl": ""
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deleteBoard_success() throws Exception {
        Member writer = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(writer)
                .title("삭제 대상")
                .content("삭제할 게시글입니다.")
                .category(BoardCategory.DAILY)
                .build()
        );

        MockHttpServletRequestBuilder request = delete("/api/boards/" + board.getId());

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));
    }
}