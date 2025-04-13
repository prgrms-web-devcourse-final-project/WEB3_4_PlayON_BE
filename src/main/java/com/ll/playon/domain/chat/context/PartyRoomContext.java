package com.ll.playon.domain.chat.context;

import com.ll.playon.domain.chat.entity.PartyRoom;
import org.springframework.stereotype.Component;

@Component
public class PartyRoomContext {
    private static final ThreadLocal<PartyRoom> currentPartyRoom = new ThreadLocal<>();

    public static PartyRoom getPartyRoom() {
        return currentPartyRoom.get();
    }

    public static void setPartyRoom(PartyRoom partyRoom) {
        currentPartyRoom.set(partyRoom);
    }

    public static void clear() {
        currentPartyRoom.remove();
    }
}
