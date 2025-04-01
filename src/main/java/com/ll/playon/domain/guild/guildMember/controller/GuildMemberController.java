package com.ll.playon.domain.guild.guildMember.controller;

import com.ll.playon.domain.guild.guildMember.dto.request.*;
import com.ll.playon.domain.guild.guildMember.dto.response.GuildMemberResponse;
import com.ll.playon.domain.guild.guildMember.service.GuildMemberService;
import com.ll.playon.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guilds")
public class GuildMemberController {

    private final GuildMemberService guildMemberService;

    @GetMapping("/{guildId}/members")
    public RsData<List<GuildMemberResponse>> getGuildMembers(@PathVariable Long guildId) {
        return RsData.success(HttpStatus.OK, guildMemberService.getAllGuildMembers(guildId));
    }

    @DeleteMapping("/{guildId}/members/leave")
    public RsData<String> leaveGuild(@PathVariable Long guildId,
                                     @RequestBody(required = false) LeaveGuildRequest request) {
        guildMemberService.leaveGuild(guildId, request);
        return RsData.success(HttpStatus.OK, "길드를 탈퇴했습니다.");
    }

    @PutMapping("/{guildId}/managers")
    public RsData<String> assignManager(@PathVariable Long guildId,
                                        @RequestBody AssignManagerRequest request) {
        guildMemberService.assignManagerRole(guildId, request);
        return RsData.success(HttpStatus.OK, "운영진 권한이 부여되었습니다.");
    }

    @DeleteMapping("/{guildId}/managers")
    public RsData<String> revokeManager(@PathVariable Long guildId,
                                        @RequestBody RevokeManagerRequest request) {
        guildMemberService.revokeManagerRole(guildId, request);
        return RsData.success(HttpStatus.OK, "운영진 권한이 회수되었습니다.");
    }

    @DeleteMapping("/{guildId}/members")
    public RsData<String> expelMember(@PathVariable Long guildId,
                                      @RequestBody ExpelMemberRequest request) {
        guildMemberService.expelMember(guildId, request);
        return RsData.success(HttpStatus.OK, "길드 멤버를 강제 퇴출했습니다.");
    }

    //닉네임을 기반으로 길드에 멤버 초대
//    @PostMapping("/invite")
//    public RsData<String> inviteMember(@RequestBody InviteMemberRequest request) {
//        guildMemberService.inviteMember(request);
//        return RsData.success(HttpStatus.OK, "길드에 멤버를 초대했습니다.");
//    }
}
