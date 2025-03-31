package com.ll.playon.domain.image.mapper;

import com.ll.playon.domain.image.entity.Image;
import com.ll.playon.domain.image.type.ImageType;

public class ImageMapper {
    public static Image of(String imageUrl, long referenceId, ImageType imageType) {
        return Image.builder()
                .imageUrl(imageUrl)
                .referenceId(referenceId)
                .build();
    }
}
