package com.ll.playon.domain.guild.guild.enums;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;

import java.time.LocalDateTime;

public record GuildDetailDto(
        Long id,
        String name,
        String description,
        int memberCount,
        int maxMembers,
        boolean isPublic,
        String guildImg,
        PartyStyle partyStyle,
        GameSkill gameSkill,
        GenderFilter genderFilter,
        FriendType friendType,
        ActiveTime activeTime,
        Long gameId,
        LocalDateTime createdAt,
        GuildRole myRole
) {
    public static GuildDetailDto from(Guild guild, GuildRole myRole) {
        return new GuildDetailDto(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getMembers().size(),
                guild.getMaxMembers(),
                guild.isPublic(),
                guild.getGuildImg(),
                guild.getPartyStyle(),
                guild.getGameSkill(),
                guild.getGenderFilter(),
                guild.getFriendType(),
                guild.getActiveTime(),
                guild.getGame(),
                guild.getCreatedAt(),
                myRole
        );
    }
}
