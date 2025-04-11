package com.ll.playon.domain.board.dto;

import lombok.Builder;

@Builder
public record MemberProfileDto(
        Long memberId,
        String nickname, // 닉네임 없으면 username
        String profileImg,
        String title
) {
}
