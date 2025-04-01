package com.ll.playon.domain.guild.guild.dto;

import com.ll.playon.domain.guild.guild.enums.ActiveTime;
import com.ll.playon.domain.guild.guild.enums.GameSkill;
import com.ll.playon.domain.guild.guild.enums.GenderFilter;
import com.ll.playon.domain.guild.guild.enums.PartyStyle;
import jakarta.validation.constraints.*;

public record PostGuildRequest(
        @NotBlank(message = "길드 이름은 필수입니다.")
        @Size(max = 50, message = "길드 이름은 최대 50자까지 가능합니다.")
        String name,

        @Size(max = 500, message = "길드 소개는 최대 500자까지 가능합니다.")
        String description,

        @Min(value = 1, message = "최소 인원은 1명 이상이어야 합니다.")
        @Max(value = 100, message = "최대 인원은 100명 이하로 설정해주세요.")
        int maxMembers,

        boolean isPublic,

        Long gameId,

        @NotBlank(message = "길드 대표 이미지는 필수입니다.")
        String guildImg,

        @NotNull(message = "파티 스타일을 선택해주세요.")
        PartyStyle partyStyle,

        @NotNull(message = "게임 실력을 선택해주세요.")
        GameSkill gameSkill,

        @NotNull(message = "성별 조건을 선택해주세요.")
        GenderFilter genderFilter,

        @NotNull(message = "활동 시간을 선택해주세요.")
        ActiveTime activeTime
) {
}
