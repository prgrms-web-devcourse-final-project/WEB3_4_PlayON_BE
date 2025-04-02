package com.ll.playon.domain.member.dto;

import com.ll.playon.domain.member.entity.enums.Gender;
import com.ll.playon.domain.member.entity.enums.PlayStyle;
import com.ll.playon.domain.member.entity.enums.SkillLevel;
import jakarta.validation.constraints.NotBlank;

public record MemberDetailDto(
        @NotBlank(message = "닉네임을 입력하세요.") String nickname,
        @NotBlank(message = "프로필 이미지를 입력하세요.") String profileImg,
        @NotBlank(message = "플레이 스타일을 입력하세요.") PlayStyle playStyle,
        @NotBlank(message = "게임실력을 입력하세요.") SkillLevel skillLevel,
        @NotBlank(message = "성별을 입력하세요.") Gender gender
) {}
