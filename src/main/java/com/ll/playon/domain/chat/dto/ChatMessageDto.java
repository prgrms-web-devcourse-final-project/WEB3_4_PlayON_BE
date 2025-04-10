package com.ll.playon.domain.chat.dto;

import com.ll.playon.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.NonNull;

public record ChatMessageDto(
        long senderMemberId,

        String title,

        @NotBlank
        String nickname,

        @NonNull
        String profileImg,

        @NotBlank
        String message,

        @NonNull
        LocalDateTime sendAt
) {
    public static ChatMessageDto of(Member member, String title, String message) {
        return new ChatMessageDto(
                member.getId(),
                title,
                member.getNickname(),
                member.getProfileImg(),
                message,
                LocalDateTime.now()
        );
    }

    public static ChatMessageDto enter(Member member, String title) {
        return new ChatMessageDto(
                member.getId(),
                title,
                member.getNickname(),
                member.getProfileImg(),
                "[ " + member.getNickname() + " ] 님이 입장하셨습니다.",
                LocalDateTime.now()
        );
    }

    public static ChatMessageDto leave(Member member, String title) {
        return new ChatMessageDto(
                member.getId(),
                title,
                member.getNickname(),
                member.getProfileImg(),
                "[ " + member.getNickname() + " ]님이 퇴장하셨습니다.",
                LocalDateTime.now()
        );
    }
}
