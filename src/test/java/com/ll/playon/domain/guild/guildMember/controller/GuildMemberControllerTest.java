package com.ll.playon.domain.guild.guildMember.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.GuildTag;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildMember.dto.request.*;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.global.steamAPI.SteamAPI;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GuildMemberControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberRepository memberRepository;
    @Autowired private GuildRepository guildRepository;
    @Autowired private GuildMemberRepository guildMemberRepository;
    @MockitoBean private SteamAPI steamAPI;

    @Autowired
    private TestMemberHelper testMemberHelper;

    private Member leader, manager, member;
    private Guild guild;

    @BeforeEach
    void setUp() {
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


        saveGuildMember(leader, GuildRole.LEADER);
        saveGuildMember(manager, GuildRole.MANAGER);
        saveGuildMember(member, GuildRole.MEMBER);

        guild = guildRepository.findById(guild.getId()).orElseThrow();
    }

    private Member saveMember(String username, Long steamId) {
        return memberRepository.save(Member.builder().username(username).steamId(steamId)
                .role(Role.USER).apiKey(String.valueOf(UUID.randomUUID())).build());
    }

    private void saveGuildMember(Member member, GuildRole role) {
        guildMemberRepository.save(GuildMember.builder().guild(guild).member(member).guildRole(role).build());
    }

    private String createJson(Object dto) throws Exception {
        return objectMapper.writeValueAsString(dto);
    }

    private ResultActions performRequest(Member member, String method, String urlTemplate, Object dto) throws Exception {
        var builder = switch (method) {
            case "GET" -> get(urlTemplate);
            case "POST" -> post(urlTemplate);
            case "PUT" -> put(urlTemplate);
            case "DELETE" -> delete(urlTemplate);
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };

        if (dto != null) {
            builder = builder.contentType(MediaType.APPLICATION_JSON).content(createJson(dto));
        }

        return testMemberHelper.requestWithUserAuth(member.getUsername(), builder);
    }

    @Test @DisplayName("길드장/운영진이 멤버 조회 - 성공")
    void getMembers_success() throws Exception {
        performRequest(manager, "GET", "/api/guilds/%s/members".formatted(guild.getId()), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test @DisplayName("일반 멤버가 멤버 조회 - 실패")
    void getMembers_fail_notAuthorized() throws Exception {
        performRequest(member, "GET", "/api/guilds/%s/members".formatted(guild.getId()), null)
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("승인 권한이 없습니다."));
    }

    @Test @DisplayName("길드장이 운영진 권한 부여 - 성공")
    void assignManager_success() throws Exception {
        performRequest(leader, "PUT", "/api/guilds/%s/managers".formatted(guild.getId()), new AssignManagerRequest(member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("운영진 권한이 부여되었습니다."));
    }

    @Test @DisplayName("이미 운영진인 사람에게 권한 부여 - 실패")
    void assignManager_fail_alreadyManager() throws Exception {
        performRequest(leader, "PUT", "/api/guilds/%s/managers".formatted(guild.getId()), new AssignManagerRequest(manager.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 운영진 권한을 보유하고 있습니다."));
    }

    @Test @DisplayName("운영진 권한 회수 - 성공")
    void revokeManager_success() throws Exception {
        performRequest(leader, "DELETE", "/api/guilds/%s/managers".formatted(guild.getId()), new RevokeManagerRequest(manager.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("운영진 권한이 회수되었습니다."));
    }

    @Test @DisplayName("운영진 아닌 대상 권한 회수 - 실패")
    void revokeManager_fail_notManager() throws Exception {
        performRequest(leader, "DELETE", "/api/guilds/%s/managers".formatted(guild.getId()), new RevokeManagerRequest(member.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("운영진 권한이 없는 유저입니다."));
    }

    @Test @DisplayName("일반/운영진 길드 탈퇴 - 성공")
    void leaveGuild_success() throws Exception {
        performRequest(member, "DELETE", "/api/guilds/%s/members/leave".formatted(guild.getId()), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("길드를 탈퇴했습니다."));
    }

    @Test @DisplayName("길드장이 위임 없이 탈퇴 - 실패")
    void leaveGuild_fail_leader_no_delegate() throws Exception {
        performRequest(leader, "DELETE", "/api/guilds/%s/members/leave".formatted(guild.getId()), null)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("길드장은 탈퇴할 수 없습니다."));
    }

    @Test @DisplayName("운영진이 멤버 강제 퇴출 - 성공")
    void expelMember_success() throws Exception {
        performRequest(manager, "DELETE", "/api/guilds/%s/members".formatted(guild.getId()), new ExpelMemberRequest(member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("길드 멤버를 강제 퇴출했습니다."));
    }

    @Test @DisplayName("일반 멤버가 강제 퇴출 - 실패")
    void expelMember_fail_noPermission() throws Exception {
        performRequest(member, "DELETE", "/api/guilds/%s/members".formatted(guild.getId()), new ExpelMemberRequest(manager.getId()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("길드 권한이 없습니다."));
    }

    @Test @DisplayName("길드장을 강제 추방 - 실패")
    void expelMember_fail_targetIsLeader() throws Exception {
        performRequest(manager, "DELETE", "/api/guilds/%s/members".formatted(guild.getId()), new ExpelMemberRequest(leader.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("길드장은 강제 퇴출할 수 없습니다."));
    }

    @Test @DisplayName("길드장 권한 위임 후 탈퇴 - 성공")
    void leader_delegate_then_leave_success() throws Exception {
        Member manager2 = saveMember("manager2", 4L);
        saveGuildMember(manager2, GuildRole.MANAGER);
        performRequest(leader, "DELETE", "/api/guilds/%s/members/leave".formatted(guild.getId()), new LeaveGuildRequest(manager2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("길드를 탈퇴했습니다."));
    }

    @Test @DisplayName("길드장 권한 위임 대상이 manager 아님 - 실패")
    void leader_delegate_to_non_manager_fail() throws Exception {
        performRequest(leader, "DELETE", "/api/guilds/%s/members/leave".formatted(guild.getId()), new LeaveGuildRequest(member.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("권한 위임은 운영진에게만 가능합니다."));
    }

    @Test @DisplayName("멤버 초대 - 성공")
    void inviteMember_success() throws Exception {
        Member target = saveMember("targetUser", 4L);
        performRequest(leader, "POST", "/api/guilds/%s/invite".formatted(guild.getId()), new InviteMemberRequest("targetUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("길드에 멤버를 초대했습니다."));
    }

    @Test @DisplayName("이미 가입된 유저 초대 - 실패")
    void inviteMember_fail_alreadyInGuild() throws Exception {
        Member target = saveMember("targetUser", 5L);
        saveGuildMember(target, GuildRole.MEMBER);
        performRequest(leader, "POST", "/api/guilds/%s/invite".formatted(guild.getId()), new InviteMemberRequest("targetUser"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 해당 길드에 가입한 멤버입니다."));
    }

    @Test @DisplayName("권한 없는 유저가 초대 - 실패")
    void inviteMember_fail_noPermission() throws Exception {
        Member target = saveMember("targetUser", 6L);
        performRequest(member, "POST", "/api/guilds/%s/invite".formatted(guild.getId()), new InviteMemberRequest("targetUser"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("길드 권한이 없습니다."));
    }

    @Test
    @DisplayName("멤버 조회 시 게시글 수 postCount 포함 - 성공")
    void getMembers_withPostCount_success() throws Exception {
        // 게시글을 작성한 멤버 추가 (예: leader가 글 3개)
        GuildMember leaderGm = guildMemberRepository.findByGuildAndMember(guild, leader).orElseThrow();
        for (int i = 0; i < 3; i++) {
            guild.getBoards().add(GuildBoard.builder()
                    .guild(guild)
                    .author(leaderGm)
                    .title("제목" + i)
                    .content("내용" + i)
                    .tag(BoardTag.FREE)
                    .build());
        }

        testMemberHelper.requestWithUserAuth(manager.getUsername(),
                        get("/api/guilds/{guildId}/members", guild.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.memberId == %d)].postCount".formatted(leader.getId())).value(3));
    }
}
