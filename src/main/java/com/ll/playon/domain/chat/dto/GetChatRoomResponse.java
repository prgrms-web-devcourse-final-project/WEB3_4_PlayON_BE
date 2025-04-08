package com.ll.playon.domain.chat.dto;

import java.util.List;
import lombok.NonNull;

public record GetChatRoomResponse(
        long partyRoomId,
        long partyId,

        @NonNull
        List<ChatMemberDto> members,

        @NonNull
        List<ChatMessageDto> messages
) {
}
