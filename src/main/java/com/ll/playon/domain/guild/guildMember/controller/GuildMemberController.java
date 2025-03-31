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
}
