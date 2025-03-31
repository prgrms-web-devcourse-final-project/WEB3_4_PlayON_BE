package com.ll.playon.domain.guild.guildMember.controller;

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
    public ResponseEntity<RsData<List<GuildMemberResponse>>> getGuildMembers(
            @PathVariable Long guildId,
            @RequestParam Long viewerId
    ) {
        List<GuildMemberResponse> members = guildMemberService.getAllGuildMembers(guildId, viewerId);
        return ResponseEntity.ok(RsData.success(HttpStatus.OK, members));
    }

    @DeleteMapping("/{guildId}/members/leave/self")
    public ResponseEntity<RsData<String>> leaveGuild(
            @PathVariable Long guildId,
            @RequestParam Long memberId,
            @RequestParam(required = false) Long newLeaderId
    ){
        guildMemberService.leaveGuild(guildId, memberId, newLeaderId);
        return ResponseEntity.ok(RsData.success(HttpStatus.OK, "길드를 탈퇴했습니다."));
    }

    @PutMapping("/{guildId}/managers/{memberId}")
    public ResponseEntity<RsData<String>> assignManager(
            @PathVariable Long guildId,
            @PathVariable Long memberId,
            @RequestParam Long requesterId
    ) {
        guildMemberService.assignManagerRole(guildId, memberId, requesterId);
        return ResponseEntity.ok(RsData.success(HttpStatus.OK, "운영진 권한이 부여되었습니다."));
    }

    @DeleteMapping("/{guildId}/managers/{memberId}")
    public ResponseEntity<RsData<String>> revokeManager(
            @PathVariable Long guildId,
            @PathVariable Long memberId,
            @RequestParam Long requesterId
    ) {
        guildMemberService.revokeManagerRole(guildId, memberId, requesterId);
        return ResponseEntity.ok(RsData.success(HttpStatus.OK, "운영진 권한이 회수되었습니다."));
    }
}
