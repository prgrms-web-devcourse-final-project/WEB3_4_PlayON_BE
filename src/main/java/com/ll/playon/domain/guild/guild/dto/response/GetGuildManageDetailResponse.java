package com.ll.playon.domain.guild.guild.dto.response;

import com.ll.playon.domain.guild.guild.entity.Guild;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GetGuildManageDetailResponse(
        Long id,
        String name,
        String leaderName,
        List<String> managerNames,
        int memberCount,
        String guildImg,
        LocalDateTime createdAt,
        String myRole,
        List<GuildTagResponse> tags
) {
    public static GetGuildManageDetailResponse from(Guild guild, String myRole, List<String> managerNames) {
        return GetGuildManageDetailResponse.builder()
                .id(guild.getId())
                .name(guild.getName())
                .leaderName(guild.getOwner().getNickname())
                .managerNames(managerNames)
                .memberCount(guild.getMembers().size())
                .guildImg(guild.getGuildImg())
                .createdAt(guild.getCreatedAt())
                .myRole(myRole)
                .tags(GuildTagResponse.fromList(guild.getGuildTags()))
                .build();
    }
}
