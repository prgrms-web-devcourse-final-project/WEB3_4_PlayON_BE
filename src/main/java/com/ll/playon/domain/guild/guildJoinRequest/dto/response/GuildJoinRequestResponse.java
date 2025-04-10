package com.ll.playon.domain.guild.guildJoinRequest.dto.response;

import com.ll.playon.domain.guild.guildJoinRequest.entity.GuildJoinRequest;
import com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState;

import java.time.LocalDateTime;

public record GuildJoinRequestResponse(
        Long requestId,
        Long memberId,
        String username,
        String titleName,
        String profileImg,
        LocalDateTime requestedAt,
        ApprovalState approvalState
) {
    public static GuildJoinRequestResponse from(GuildJoinRequest entity, String titleName) {
        return new GuildJoinRequestResponse(
                entity.getId(),
                entity.getMember().getId(),
                entity.getMember().getUsername(),
                titleName,
                entity.getMember().getProfileImg(),
                entity.getCreatedAt(),
                entity.getApprovalState()
        );
    }
}
