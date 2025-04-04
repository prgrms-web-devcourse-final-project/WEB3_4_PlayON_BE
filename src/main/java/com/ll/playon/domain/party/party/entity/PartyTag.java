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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "party_tag",
        indexes = {
                @Index(name = "idx_party_tag_party_id_value", columnList = "party_id, tag_value"),
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
    @Column(name = "tag_type")
    private TagType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_value")
    private TagValue value;

    // TODO: 더 좋은 방법이 있나 고민해볼 것
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartyTag partyTag = (PartyTag) o;

        return this.type == partyTag.type && value == partyTag.value && party.equals(partyTag.party);
    }

    @Override
    public int hashCode() {
        return Objects.hash(party, type, value);
    }

}
