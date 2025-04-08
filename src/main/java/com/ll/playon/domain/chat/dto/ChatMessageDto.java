package com.ll.playon.domain.chat.dto;

import com.ll.playon.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.NonNull;

public record ChatMessageDto(
        long senderMemberId,

        // TODO: 칭호 연동 후 주석 해제
//        String title,

        @NotBlank
        String nickname,

        @NonNull
        String profileImg,

        @NotBlank
        String message,

        @NonNull
        LocalDateTime sendAt
) {
    public static ChatMessageDto of(Member member, String message) {
        return new ChatMessageDto(
                member.getId(),
//                member.getTitle(),
                member.getNickname(),
                member.getProfileImg(),
                message,
                LocalDateTime.now()
        );
    }

    public static ChatMessageDto enter(Member member) {
        return new ChatMessageDto(
                member.getId(),
//                member.getTitle(),
                member.getNickname(),
                member.getProfileImg(),
                "[" + member.getNickname() + " ]님이 입장하셨습니다.",
                LocalDateTime.now()
        );
    }

    public static ChatMessageDto leave(Member member) {
        return new ChatMessageDto(
                member.getId(),
//                member.getTitle(),
                member.getNickname(),
                member.getProfileImg(),
                "[" + member.getNickname() + " ]님이 퇴장하셨습니다.",
                LocalDateTime.now()
        );
    }
}
