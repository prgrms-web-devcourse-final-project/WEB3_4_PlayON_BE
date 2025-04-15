package com.ll.playon.domain.board.controller;

import com.ll.playon.domain.board.entity.Board;
import com.ll.playon.domain.board.entity.BoardComment;
import com.ll.playon.domain.board.enums.BoardCategory;
import com.ll.playon.domain.board.repository.BoardCommentRepository;
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
class BoardCommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardCommentRepository boardCommentRepository;

    @Test
    @DisplayName("댓글 작성 성공")
    void createComment_success() throws Exception {
        Member writer = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(writer)
                .title("게시글 제목")
                .content("게시글 내용")
                .category(BoardCategory.DAILY)
                .build());

        MockHttpServletRequestBuilder request = post("/api/boards/" + board.getId() + "/comments")
                .content("""
                        {
                            "comment": "댓글 내용"
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions.andExpect(status().isCreated())
                .andExpect(handler().handlerType(BoardCommentController.class));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_success() throws Exception {
        Member writer = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(writer)
                .title("게시글 제목")
                .content("게시글 내용")
                .category(BoardCategory.DAILY)
                .build());

        BoardComment comment = boardCommentRepository.save(BoardComment.builder()
                .author(writer)
                .board(board)
                .comment("수정 전 댓글")
                .build());

        MockHttpServletRequestBuilder request = put("/api/boards/" + board.getId() + "/comments/" + comment.getId())
                .content("""
                        {
                            "comment": "수정된 댓글"
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions.andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardCommentController.class))
                .andExpect(jsonPath("$.data.commentId").value(comment.getId()));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success() throws Exception {
        Member writer = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(writer)
                .title("게시글 제목")
                .content("게시글 내용")
                .category(BoardCategory.DAILY)
                .build());

        BoardComment comment = boardCommentRepository.save(BoardComment.builder()
                .author(writer)
                .board(board)
                .comment("삭제할 댓글")
                .build());

        MockHttpServletRequestBuilder request = delete("/api/boards/" + board.getId() + "/comments/" + comment.getId());

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser1", request);

        resultActions.andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardCommentController.class));
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void getComments_success() throws Exception {
        Member writer = memberRepository.findByUsername("sampleUser1").get();

        Board board = boardRepository.save(Board.builder()
                .author(writer)
                .title("게시글 제목")
                .content("게시글 내용")
                .category(BoardCategory.DAILY)
                .build());

        boardCommentRepository.save(BoardComment.builder()
                .author(writer)
                .board(board)
                .comment("댓글1")
                .build());

        boardCommentRepository.save(BoardComment.builder()
                .author(writer)
                .board(board)
                .comment("댓글2")
                .build());

        MockHttpServletRequestBuilder request = get("/api/boards/" + board.getId() + "/comments")
                .param("page", "1")
                .param("pageSize", "10");

        ResultActions resultActions = testMemberHelper.requestWithUserAuth("sampleUser2", request);

        resultActions.andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardCommentController.class))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(2));
    }
}
