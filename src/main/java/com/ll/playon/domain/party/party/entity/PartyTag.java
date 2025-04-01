package com.ll.playon.domain.party.party.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "partyTags",
        indexes = {
                @Index(name = "idx_party_tag_type_value", columnList = "tagType, tagValue"),
                @Index(name = "idx_party_tag_party_id", columnList = "party_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PartyTag extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Party party;

    @Enumerated(EnumType.STRING)
    @Column(name = "tagType")
    private TagType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "tagValue")
    private TagValue value;
}
