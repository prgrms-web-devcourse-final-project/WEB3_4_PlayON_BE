package com.ll.playon.domain.party.party.service;

import com.ll.playon.domain.party.party.dto.request.PartyTagRequest;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.mapper.PartyTagMapper;
import com.ll.playon.domain.party.party.repository.PartyTagRepository;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyTagService {
    private final PartyTagRepository partyTagRepository;

    // PartyTag 업데이트
    public void updatePartyTags(Party party, List<PartyTagRequest> tagRequests) {
        Set<PartyTag> existingPartyTags = new HashSet<>(party.getPartyTags());

        // 수정 요청의 PartyTags
        Set<PartyTag> newPartyTags = tagRequests.stream()
                .map(tagRequest -> PartyTagMapper.build(party, TagType.fromValue(tagRequest.type()),
                        TagValue.fromValue(tagRequest.value())))
                .collect(Collectors.toSet());

        // 삭제할 PartyTags
        Set<PartyTag> removePartyTags = existingPartyTags.stream()
                .filter(tag -> !newPartyTags.contains(tag))
                .collect(Collectors.toSet());

        // 추가할 PartyTags
        Set<PartyTag> addPartyTags = newPartyTags.stream()
                .filter(tag -> !existingPartyTags.contains(tag))
                .collect(Collectors.toSet());

        this.deleteOldTags(party, removePartyTags);
        party.getPartyTags().addAll(addPartyTags);
    }

    // PartyTag 삭제
    public void deleteOldTags(Party party, Set<PartyTag> oldTags) {
        for (PartyTag oldTag : oldTags) {
            oldTag.setParty(null);
            party.getPartyTags().remove(oldTag);
        }

        this.partyTagRepository.deleteAll(oldTags);
    }
}
