package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guild.entity.Guild;

import java.util.HashMap;
import java.util.Map;

public record GetGuildListResponse(
        String guildImg,
        String name,
        String description,
        Map<String, String> tags,
        int memberCount
) {
    public static GetGuildListResponse from(Guild guild) {
        Map<String, String> tags = new HashMap<>();

        if (guild.getPartyStyle() != null) {
            tags.put("partyStyle", guild.getPartyStyle().getLabel());
        }

        if (guild.getGameSkill() != null) {
            tags.put("gameSkill", guild.getGameSkill().getLabel());
        }

        if (guild.getGenderFilter() != null) {
            tags.put("genderFilter", guild.getGenderFilter().getLabel());
        }

        if (guild.getActiveTime() != null) {
            tags.put("activeTime", guild.getActiveTime().getLabel());
        }

        return new GetGuildListResponse(
                guild.getGuildImg(),
                guild.getName(),
                guild.getDescription(),
                tags,
                guild.getMembers().size()
        );
    }
}