package com.ll.playon.domain.guild.guildBoard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCommentCreateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCommentUpdateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCreateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardUpdateRequest;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardComment;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardCommentRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.global.security.UserContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class GuildBoardControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private GuildRepository guildRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private GuildMemberRepository guildMemberRepository;
    @Autowired private GuildBoardRepository guildBoardRepository;
    @Autowired private GuildBoardCommentRepository guildBoardCommentRepository;
    @MockBean private UserContext userContext;

    private Member member;
    private Guild guild;
    private GuildBoard board;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder().username("tester").steamId(1L).build());
        guild = guildRepository.save(Guild.builder().name("TestGuild").owner(member).maxMembers(10).isPublic(true).build());
        guildMemberRepository.save(GuildMember.builder().guild(guild).member(member).guildRole(GuildRole.LEADER).build());

        board = guildBoardRepository.save(GuildBoard.builder()
                .guild(guild)
                .author(guildMemberRepository.findByGuildAndMember(guild, member).orElseThrow())
                .title("제목")
                .content("내용")
                .tag(BoardTag.FREE)
                .build());

        given(userContext.getActor()).willReturn(member);
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @DisplayName("게시글 전체 조회 - 성공")
    void getBoards_success() throws Exception {
        mockMvc.perform(get("/api/guilds/{guildId}/board", guild.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test @DisplayName("게시글 작성 - 성공")
    void createBoard_success() throws Exception {
        var req = new GuildBoardCreateRequest("새 제목", "새 내용", BoardTag.FREE, null);

        mockMvc.perform(post("/api/guilds/{guildId}/board", guild.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test @DisplayName("게시글 작성 - 권한 없음 (공지 작성 시)")
    void createBoard_notice_fail() throws Exception {
        var gm = guildMemberRepository.findByGuildAndMember(guild, member).orElseThrow();
        gm.setGuildRole(GuildRole.MEMBER);

        var req = new GuildBoardCreateRequest("공지 제목", "공지 내용", BoardTag.NOTICE, null);

        mockMvc.perform(post("/api/guilds/{guildId}/board", guild.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("길드 권한이 없습니다."));
    }

    @Test @DisplayName("게시글 수정 - 성공")
    void updateBoard_success() throws Exception {
        var req = new GuildBoardUpdateRequest("수정된 제목", "수정된 내용", BoardTag.GAME, null);

        mockMvc.perform(put("/api/guilds/{guildId}/board/{boardId}", guild.getId(), board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("수정되었습니다."));
    }

    @Test @DisplayName("게시글 수정 - 권한 없음")
    void updateBoard_fail() throws Exception {
        Member other = memberRepository.save(Member.builder().username("other").steamId(2L).build());
        given(userContext.getActor()).willReturn(other);

        var req = new GuildBoardUpdateRequest("제목", "내용", BoardTag.FREE, null);

        mockMvc.perform(put("/api/guilds/{guildId}/board/{boardId}", guild.getId(), board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isForbidden());
    }

    @Test @DisplayName("게시글 삭제 - 성공")
    void deleteBoard_success() throws Exception {
        mockMvc.perform(delete("/api/guilds/{guildId}/board/{boardId}", guild.getId(), board.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("삭제되었습니다."));
    }

    @Test @DisplayName("게시글 삭제 - 권한 없음")
    void deleteBoard_fail() throws Exception {
        Member other = memberRepository.save(Member.builder().username("other").steamId(2L).build());
        given(userContext.getActor()).willReturn(other);

        mockMvc.perform(delete("/api/guilds/{guildId}/board/{boardId}", guild.getId(), board.getId()))
                .andExpect(status().isForbidden());
    }

    @Test @DisplayName("게시글 상세 조회 - 성공")
    void getBoardDetail_success() throws Exception {
        mockMvc.perform(get("/api/guilds/{guildId}/board/{boardId}", guild.getId(), board.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(board.getId()));
    }

    @Test @DisplayName("게시글 좋아요 토글 - 성공")
    void toggleLike_success() throws Exception {
        mockMvc.perform(post("/api/guilds/{guildId}/board/{boardId}/like", guild.getId(), board.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.liked").value(true));
    }

    @Test @DisplayName("댓글 작성 - 성공")
    void createComment_success() throws Exception {
        var req = new GuildBoardCommentCreateRequest("댓글 내용");

        mockMvc.perform(post("/api/guilds/{guildId}/board/{boardId}/comments", guild.getId(), board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test @DisplayName("댓글 수정 - 성공")
    void updateComment_success() throws Exception {
        var comment = saveComment("수정 전");
        var req = new GuildBoardCommentUpdateRequest("수정된 댓글");

        mockMvc.perform(put("/api/guilds/{guildId}/board/{boardId}/comments/{commentId}",
                        guild.getId(), board.getId(), comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isOk());
    }

    @Test @DisplayName("댓글 삭제 - 성공")
    void deleteComment_success() throws Exception {
        var comment = saveComment("삭제할 댓글");

        mockMvc.perform(delete("/api/guilds/{guildId}/board/{boardId}/comments/{commentId}",
                        guild.getId(), board.getId(), comment.getId()))
                .andExpect(status().isOk());
    }

    private GuildBoardComment saveComment(String content) {
        GuildMember gm = guildMemberRepository.findByGuildAndMember(guild, member).orElseThrow();
        return guildBoardCommentRepository.save(GuildBoardComment.builder()
                .board(board)
                .author(gm)
                .comment(content)
                .build());
    }
}
