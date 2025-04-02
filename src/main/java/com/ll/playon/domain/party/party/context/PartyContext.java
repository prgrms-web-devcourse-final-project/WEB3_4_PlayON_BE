package com.ll.playon.domain.party.party.context;

import com.ll.playon.domain.party.party.entity.Party;
import org.springframework.stereotype.Component;

@Component
public class PartyContext {
    private static final ThreadLocal<Party> currentParty = new ThreadLocal<>();

    public static Party getParty() {
        return currentParty.get();
    }

    public static void setParty(Party party) {
        currentParty.set(party);
    }

    public static void clear() {
        currentParty.remove();
    }
}
