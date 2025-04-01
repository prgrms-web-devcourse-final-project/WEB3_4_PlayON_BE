package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guild.enums.ActiveTime;
import com.ll.playon.domain.guild.guild.enums.GameSkill;
import com.ll.playon.domain.guild.guild.enums.GenderFilter;
import com.ll.playon.domain.guild.guild.enums.PartyStyle;

import java.util.List;

public record GetGuildListRequest(
        String name,
        List<Long> gameIds, // 선택한 게임 리스트
        List<PartyStyle> partyStyles,
        List<GameSkill> gameSkills,
        List<GenderFilter> genderFilters,
        List<ActiveTime> activeTimes,
        int page,
        int size,
        String sort // latest, activity, members
) {
}
