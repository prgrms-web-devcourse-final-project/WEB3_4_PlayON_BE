package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guild.enums.*;

public record PostGuildRequest(
        String name,
        String description,
        int maxMembers,
        boolean isPublic,
        Long gameId,
        String guildImg,

        PartyStyle partyStyle,
        GameSkill gameSkill,
        GenderFilter genderFilter,
        FriendType friendType,
        ActiveTime activeTime
) {
}
