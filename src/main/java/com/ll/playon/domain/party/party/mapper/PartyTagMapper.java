package com.ll.playon.domain.party.party.mapper;

import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;

public class PartyTagMapper {
    public static PartyTag build(Party party, TagType tagType, TagValue tagValue) {
        return PartyTag.builder()
                .party(party)
                .type(tagType)
                .value(tagValue)
                .build();
    }
}
