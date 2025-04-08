package com.ll.playon.domain.chat.dto;

import com.ll.playon.domain.chat.entity.ChatMember;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record ChatMemberDto(
        long memberId,

        @NotBlank
        String nickname,

        @NonNull
        String profileImg
) {
    public ChatMemberDto(ChatMember chatMember) {
        this(
                chatMember.getPartyMember().getMember().getId(),
                chatMember.getPartyMember().getMember().getNickname(),
                chatMember.getPartyMember().getMember().getProfileImg()
        );
    }
}
