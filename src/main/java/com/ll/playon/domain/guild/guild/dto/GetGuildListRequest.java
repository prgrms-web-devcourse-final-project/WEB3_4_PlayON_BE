package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guild.enums.GameSkill;
import com.ll.playon.domain.guild.guild.enums.GenderFilter;
import com.ll.playon.domain.guild.guild.enums.PartyStyle;

import java.util.List;

public record GetGuildListRequest(
        String name,
        List<Long> gameIds,
        PartyStyle partyStyle,
        GameSkill gameSkill,
        GenderFilter genderFilter,
        String sort, // latest, activity, members
        int page,
        int size
) {
}
