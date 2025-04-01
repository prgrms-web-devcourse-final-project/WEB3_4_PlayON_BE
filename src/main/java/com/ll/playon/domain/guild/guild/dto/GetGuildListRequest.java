package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guild.enums.ActiveTime;
import com.ll.playon.domain.guild.guild.enums.GameSkill;
import com.ll.playon.domain.guild.guild.enums.GenderFilter;
import com.ll.playon.domain.guild.guild.enums.PartyStyle;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public record GetGuildListRequest(
        String name,
        List<Long> gameIds, // 선택한 게임 리스트
        List<PartyStyle> partyStyles,
        List<GameSkill> gameSkills,
        List<GenderFilter> genderFilters,
        List<ActiveTime> activeTimes,

        @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
        int page,

        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 100, message = "size는 최대 100까지 가능합니다.")
        int size,

        String sort // latest, activity, members
) {
}
