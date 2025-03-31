package com.ll.playon.domain.guild.guildJoinRequest.controller;

import com.ll.playon.domain.guild.guildJoinRequest.service.GuildJoinRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guilds")
public class GuildJoinRequestController {

    private final GuildJoinRequestService guildJoinRequestService;

    @PostMapping("/{guildId}/join/{memberId}")
    public ResponseEntity<Void> joinRequest(
            @PathVariable Long guildId,
            @PathVariable Long memberId
    ) {
        guildJoinRequestService.requestToJoinGuild(guildId, memberId);
        return ResponseEntity.ok().build();
    }
}
