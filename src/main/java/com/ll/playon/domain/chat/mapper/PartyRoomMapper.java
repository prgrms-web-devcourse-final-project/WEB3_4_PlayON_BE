package com.ll.playon.domain.chat.mapper;

import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.party.party.entity.Party;

public class PartyRoomMapper {
    public static PartyRoom of(Party party) {
        return PartyRoom.builder()
                .party(party)
                .build();
    }
}
