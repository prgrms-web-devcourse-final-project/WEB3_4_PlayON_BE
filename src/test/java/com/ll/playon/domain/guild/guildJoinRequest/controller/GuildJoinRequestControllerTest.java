package com.ll.playon.domain.guild.guildJoinRequest.controller;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.GuildTag;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildJoinRequest.entity.GuildJoinRequest;
import com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState;
import com.ll.playon.domain.guild.guildJoinRequest.repository.GuildJoinRequestRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GuildJoinRequestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private GuildRepository guildRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private GuildJoinRequestRepository guildJoinRequestRepository;
    @MockBean private UserContext userContext;

    private Member leader, manager, member;
    private Guild guild;

    @BeforeEach
    void setup() {
        guildJoinRequestRepository.deleteAll();

        leader = saveMember("leader", 1L);
        manager = saveMember("manager", 2L);
        member = saveMember("member", 3L);

        guild = guildRepository.save(Guild.builder()
                .name("테스트 길드" + UUID.randomUUID())
                .owner(leader)
                .maxMembers(10)
                .isPublic(true)
                .guildTags(List.of(
                        GuildTag.builder().type(TagType.PARTY_STYLE).value(TagValue.CASUAL).build(),
                        GuildTag.builder().type(TagType.GAME_SKILL).value(TagValue.NEWBIE).build(),
                        GuildTag.builder().type(TagType.GENDER).value(TagValue.MALE).build(),
                        GuildTag.builder().type(TagType.SOCIALIZING).value(TagValue.SOCIAL_FRIENDLY).build()
                ))
                .build());

    }

    private Member saveMember(String username, Long steamId) {
        return memberRepository.save(Member.builder().username(username).steamId(steamId).build());
    }

    private void loginAs(Member actor) {
        given(userContext.getActor()).willReturn(actor);
    }

    private void addGuildMember(Member member, GuildRole role) {
        guild.getMembers().add(GuildMember.builder()
                .guild(guild)
                .member(member)
                .guildRole(role)
                .build());
    }

    private GuildJoinRequest createJoinRequest(Member member, ApprovalState state) {
        return guildJoinRequestRepository.save(GuildJoinRequest.builder()
                .guild(guild)
                .member(member)
                .approvalState(state)
                .build());
    }

    private ResultActions performRequest(String method, String path) throws Exception {
        return switch (method) {
            case "POST" -> mockMvc.perform(post(path));
            case "GET" -> mockMvc.perform(get(path));
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }

    @Test
    @DisplayName("길드가입 요청 - 성공")
    void joinGuildSuccess() throws Exception {
        loginAs(member);
        performRequest("POST", "/api/guilds/%s/join".formatted(guild.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("가입 요청 완료"));
    }

    @Test
    @DisplayName("길드가입 중복신청 - 실패")
    void joinGuildDuplicateFail() throws Exception {
        loginAs(member);
        performRequest("POST", "/api/guilds/%s/join".formatted(guild.getId())).andExpect(status().isOk());
        performRequest("POST", "/api/guilds/%s/join".formatted(guild.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string("이미 해당 길드에 가입 요청을 보냈습니다."));
    }

    @Test
    @DisplayName("가입 승인 - 성공 (leader)")
    void approveRequestSuccessLeader() throws Exception {
        GuildJoinRequest request = createJoinRequest(member, ApprovalState.PENDING);
        addGuildMember(leader, GuildRole.LEADER);
        loginAs(leader);

        performRequest("POST", "/api/guilds/%s/join/%d/approve".formatted(guild.getId(), request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("가입이 승인되었습니다."));
    }

    @Test
    @DisplayName("가입 승인 - 성공 (manager)")
    void approveRequestSuccessManager() throws Exception {
        GuildJoinRequest request = createJoinRequest(member, ApprovalState.PENDING);
        addGuildMember(manager, GuildRole.MANAGER);
        loginAs(manager);

        performRequest("POST", "/api/guilds/%s/join/%d/approve".formatted(guild.getId(), request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("가입이 승인되었습니다."));
    }

    @Test
    @DisplayName("이미 승인된 요청 승인 시도 - 실패")
    void approveAlreadyApprovedRequestFail() throws Exception {
        GuildJoinRequest request = createJoinRequest(member, ApprovalState.APPROVED);
        addGuildMember(leader, GuildRole.LEADER);
        loginAs(leader);

        performRequest("POST", "/api/guilds/%s/join/%d/approve".formatted(guild.getId(), request.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 처리된 길드 요청입니다."));
    }

    @Test
    @DisplayName("가입 승인 시 길드 ID 불일치 - 실패")
    void approveGuildIdMismatchFail() throws Exception {
        Guild otherGuild = guildRepository.save(Guild.builder()
                .name("다른 길드")
                .owner(leader)
                .maxMembers(10)
                .isPublic(true)
                .guildTags(List.of(
                        GuildTag.builder().type(TagType.PARTY_STYLE).value(TagValue.CASUAL).build(),
                        GuildTag.builder().type(TagType.GAME_SKILL).value(TagValue.NEWBIE).build(),
                        GuildTag.builder().type(TagType.GENDER).value(TagValue.MALE).build(),
                        GuildTag.builder().type(TagType.SOCIALIZING).value(TagValue.SOCIAL_FRIENDLY).build()
                ))
                .build());

        GuildJoinRequest request = createJoinRequest(member, ApprovalState.PENDING);
        addGuildMember(leader, GuildRole.LEADER);
        loginAs(leader);

        performRequest("POST", "/api/guilds/%s/join/%d/approve".formatted(otherGuild.getId(), request.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("요청한 길드와 일치하지 않습니다."));
    }

    @Test
    @DisplayName("존재하지 않는 요청 ID - 승인 실패")
    void approveRequestNotFoundFail() throws Exception {
        addGuildMember(leader, GuildRole.LEADER);
        loginAs(leader);

        performRequest("POST", "/api/guilds/%s/join/99999/approve".formatted(guild.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("가입 요청을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("가입 요청 거절 - 성공 (manager)")
    void rejectRequestSuccessManager() throws Exception {
        GuildJoinRequest request = createJoinRequest(member, ApprovalState.PENDING);
        addGuildMember(manager, GuildRole.MANAGER);
        loginAs(manager);

        performRequest("POST", "/api/guilds/%s/join/%d/reject".formatted(guild.getId(), request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("가입 요청이 거절되었습니다."));
    }

    @Test
    @DisplayName("가입 요청 목록 조회 - 성공 (manager)")
    void getJoinRequestsManagerSuccess() throws Exception {
        createJoinRequest(member, ApprovalState.PENDING);
        addGuildMember(manager, GuildRole.MANAGER);
        loginAs(manager);

        performRequest("GET", "/api/guilds/%s/join/requests".formatted(guild.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("일반 멤버는 목록 조회 불가 - 실패")
    void getJoinRequestsUnauthorized() throws Exception {
        createJoinRequest(member, ApprovalState.PENDING);
        loginAs(member);

        performRequest("GET", "/api/guilds/%s/join/requests".formatted(guild.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("승인 권한이 없습니다."));
    }
}
