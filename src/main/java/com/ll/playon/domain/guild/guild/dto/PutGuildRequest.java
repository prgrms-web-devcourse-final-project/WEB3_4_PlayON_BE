package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guild.enums.*;

public record PutGuildRequest(
        String name,
        String description,
        int maxMembers,
        boolean isPublic,
        String guildImg,

        PartyStyle partyStyle,
        GameSkill gameSkill,
        Gender gender,
        FriendType friendType,
        ActiveTime activeTime
) {
}
