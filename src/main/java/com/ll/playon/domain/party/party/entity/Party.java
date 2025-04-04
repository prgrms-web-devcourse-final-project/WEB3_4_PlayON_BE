package com.ll.playon.domain.party.party.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ll.playon.domain.party.party.type.PartyStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "party",
        indexes = {
                @Index(name = "idx_party_status_party_at_created_at", columnList = "partyStatus, partyAt, createdAt")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Party {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    // TODO : Game 엔티티 개설되면 연결
    @Column(nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false)
    private Long game;  // gameId

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime partyAt;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private long hit;

    @Column(nullable = false)
    private int minimum;

    @Column(nullable = false)
    private int maximum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyStatus partyStatus;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyMember> partyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyTag> partyTags = new ArrayList<>();

    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime modifiedAt;

    // TODO : 파티 룸

    @Builder
    public Party(Long game, String name, String description, LocalDateTime partyAt, boolean isPublic, int minimum,
                 int maximum) {
        this.game = game;
        this.name = name;
        this.description = description != null ? description : "";
        this.partyAt = partyAt;
        this.isPublic = isPublic;
        this.hit = 0;
        this.minimum = minimum;
        this.maximum = maximum;
        this.partyStatus = PartyStatus.PENDING;
    }

    public void addPartyMember(PartyMember partyMember) {
        this.partyMembers.add(partyMember);
        partyMember.setParty(this);
    }
}
