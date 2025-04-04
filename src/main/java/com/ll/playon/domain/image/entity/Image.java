package com.ll.playon.domain.image.entity;

import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "image",
        indexes = {
                @Index(name = "idx_image_reference_id_image_type", columnList = "referenceId, imageType")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTime {
    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private long referenceId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Builder
    public Image(String imageUrl, long referenceId, ImageType imageType) {
        this.imageUrl = imageUrl;
        this.referenceId = referenceId;
        this.imageType = imageType;
    }
}
