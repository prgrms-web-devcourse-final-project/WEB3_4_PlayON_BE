package com.ll.playon.domain.party.party.dto.request;

import com.ll.playon.global.type.TagValue;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.util.CollectionUtils;

public record GetAllPartiesRequest(
        Long appId,

        List<String> genres,

        List<@Valid PartyTagRequest> tags
) {
    public GetAllPartiesRequest {
        genres = CollectionUtils.isEmpty(genres) ? null : genres;
        tags = CollectionUtils.isEmpty(tags) ? null : tags;
    }

    public List<String> getTagValues() {
        return tags != null ? tags.stream().map(tag -> TagValue.fromValue(tag.value()).name()).toList() : null;
    }
}
