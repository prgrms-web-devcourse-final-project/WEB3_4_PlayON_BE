package com.ll.playon.domain.image.event;

import com.ll.playon.domain.image.type.ImageType;

public record ImageDeleteEvent(
        long id,
        ImageType imageType
) {
}
