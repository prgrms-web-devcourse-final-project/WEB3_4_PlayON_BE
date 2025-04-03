package com.ll.playon.domain.guild.guildMember.controller;

import com.ll.playon.domain.guild.guildMember.dto.request.*;
import com.ll.playon.domain.guild.guildMember.dto.response.GuildMemberResponse;
import com.ll.playon.domain.guild.guildMember.service.GuildMemberService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guilds")
public class GuildMemberController {

    private final GuildMemberService guildMemberService;
    private final UserContext userContext;

    @GetMapping("/{guildId}/members")
    public RsData<List<GuildMemberResponse>> getGuildMembers(@PathVariable Long guildId) {
        Member actor = userContext.getActor();
        return RsData.success(HttpStatus.OK, guildMemberService.getAllGuildMembers(guildId, actor));
    }

    @DeleteMapping("/{guildId}/members/leave")
    public RsData<String> leaveGuild(@PathVariable Long guildId,
                                     @RequestBody(required = false) LeaveGuildRequest request) {
        Member actor = userContext.getActor();
        guildMemberService.leaveGuild(guildId, actor, request);
        return RsData.success(HttpStatus.OK, "길드를 탈퇴했습니다.");
    }

    @PutMapping("/{guildId}/managers")
    public RsData<String> assignManager(@PathVariable Long guildId,
                                        @RequestBody AssignManagerRequest request) {
        Member actor = userContext.getActor();
        guildMemberService.assignManagerRole(guildId, actor, request);
        return RsData.success(HttpStatus.OK, "운영진 권한이 부여되었습니다.");
    }

    @DeleteMapping("/{guildId}/managers")
    public RsData<String> revokeManager(@PathVariable Long guildId,
                                        @RequestBody RevokeManagerRequest request) {
        Member actor = userContext.getActor();
        guildMemberService.revokeManagerRole(guildId, actor, request);
        return RsData.success(HttpStatus.OK, "운영진 권한이 회수되었습니다.");
    }

    @DeleteMapping("/{guildId}/members")
    public RsData<String> expelMember(@PathVariable Long guildId,
                                      @RequestBody ExpelMemberRequest request) {
        Member actor = userContext.getActor();
        guildMemberService.expelMember(guildId, actor, request);
        return RsData.success(HttpStatus.OK, "길드 멤버를 강제 퇴출했습니다.");
    }

    //닉네임을 기반으로 길드에 멤버 초대
//    @PostMapping("/invite")
//    public RsData<String> inviteMember(@RequestBody InviteMemberRequest request) {
//        Member actor = userContext.getActor();
//        guildMemberService.inviteMember(actor, request);
//        return RsData.success(HttpStatus.OK, "길드에 멤버를 초대했습니다.");
//    }
}
