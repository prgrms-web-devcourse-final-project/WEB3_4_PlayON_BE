package com.ll.playon.domain.party.party.context;

import com.ll.playon.domain.party.party.entity.PartyMember;
import org.springframework.stereotype.Component;

@Component
public class PartyMemberContext {
    private static final ThreadLocal<PartyMember> currentPartyMember = new ThreadLocal<>();

    public static PartyMember getPartyMember() {
        return currentPartyMember.get();
    }

    public static void setPartyMember(PartyMember partyMember) {
        currentPartyMember.set(partyMember);
    }

    public static void clear() {
        currentPartyMember.remove();
    }
}
