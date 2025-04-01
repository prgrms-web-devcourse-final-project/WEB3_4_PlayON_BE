package com.ll.playon.domain.party.party.service;

import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.response.PostPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.mapper.PartyMapper;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;

    @Transactional
    public PostPartyResponse createParty(PostPartyRequest request) {
        Party party = PartyMapper.of(request);

        List<PartyTag> partyTags = this.createPartyTag(request, party);
        party.setPartyTags(partyTags);

        return new PostPartyResponse(this.partyRepository.save(party));
    }

    // 요청으로부터 파티 태그 리스트 생성
    private List<PartyTag> createPartyTag(PostPartyRequest request, Party party) {
        return request.tags().stream()
                .map(tag -> PartyTag.builder()
                        .party(party)
                        .type(TagType.fromValue(tag.type()))
                        .value(TagValue.fromValue(tag.value()))
                        .build())
                .toList();
    }
}
