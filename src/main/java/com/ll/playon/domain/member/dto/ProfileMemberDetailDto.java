package com.ll.playon.domain.member.dto;

import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.member.entity.enums.Gender;
import com.ll.playon.domain.member.entity.enums.PlayStyle;
import com.ll.playon.domain.member.entity.enums.SkillLevel;

import java.time.LocalDateTime;

public record ProfileMemberDetailDto(
        Long steamId,
        String username,
        String nickname,
        String profileImg,
        LocalDateTime lastLoginAt,
        PlayStyle playStyle,
        SkillLevel skillLevel,
        Gender gender,
        SteamGenre preferredGenre
){}
