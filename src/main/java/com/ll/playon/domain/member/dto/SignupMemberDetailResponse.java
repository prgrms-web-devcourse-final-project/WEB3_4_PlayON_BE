package com.ll.playon.domain.member.dto;

import com.ll.playon.domain.member.entity.enums.Gender;
import com.ll.playon.domain.member.entity.enums.PlayStyle;
import com.ll.playon.domain.member.entity.enums.SkillLevel;

public record SignupMemberDetailResponse(
    String nickname,
    String profileImg,
    PlayStyle play_style,
    SkillLevel skillLevel,
    Gender gender
) {}
