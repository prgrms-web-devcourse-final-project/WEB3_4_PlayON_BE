package com.ll.playon.domain.chat.mapper;

import com.ll.playon.domain.chat.entity.ChatMember;
import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.party.party.entity.PartyMember;

public class ChatMemberMapper {
    public static ChatMember of(PartyRoom partyRoom, PartyMember partyMember) {
        return ChatMember.builder()
                .partyRoom(partyRoom)
                .partyMember(partyMember)
                .build();
    }
}
