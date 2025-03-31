package com.ll.playon.domain.guild.guildJoinRequest.controller;

import com.ll.playon.domain.guild.guildJoinRequest.dto.request.GuildJoinApproveRequest;
import com.ll.playon.domain.guild.guildJoinRequest.service.GuildJoinRequestService;
import com.ll.playon.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/{guildId}/join/{requestId}/approve")
    public ResponseEntity<RsData<String>> approveRequest(
            @PathVariable Long guildId,
            @PathVariable Long requestId,
            @RequestBody GuildJoinApproveRequest request
    ) {
        guildJoinRequestService.approveJoinRequest(guildId, requestId, request);
        return ResponseEntity.ok(RsData.success(HttpStatus.OK, "가입이 승인되었습니다."));
    }
}
