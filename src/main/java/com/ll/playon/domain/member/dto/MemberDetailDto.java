package com.ll.playon.domain.member.dto;

import com.ll.playon.domain.member.entity.enums.Gender;
import com.ll.playon.domain.member.entity.enums.PlayStyle;
import com.ll.playon.domain.member.entity.enums.SkillLevel;
import jakarta.validation.constraints.NotBlank;

public record MemberDetailDto(
        @NotBlank(message = "닉네임을 입력하세요.") String nickname,
        String profileImg,
        PlayStyle playStyle,
        SkillLevel skillLevel,
        Gender gender
) {}
