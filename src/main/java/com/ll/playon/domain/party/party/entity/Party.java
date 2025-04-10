package com.ll.playon.domain.party.party.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.party.party.dto.request.PutPartyRequest;
import com.ll.playon.domain.party.party.type.PartyStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
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
                @Index(name = "idx_party_status_public_party_at_id", columnList = "party_status, is_public, party_at, id"),
                @Index(name = "idx_party_id_party_at", columnList = "id, party_at")
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

    @ManyToOne(fetch = FetchType.LAZY)
    private SteamGame game;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime partyAt;

    @Column
    private LocalDateTime endedAt;

    @Column(name = "is_public", nullable = false)
    private boolean publicFlag;

    @Column(nullable = false)
    private long hit;

    @Column(nullable = false)
    private int minimum;

    @Column(nullable = false)
    private int maximum;

    @Column(nullable = false)
    private int total;

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

    @Builder
    public Party(SteamGame game, String name, String description, LocalDateTime partyAt, boolean publicFlag,
                 int minimum,
                 int maximum) {
        this.game = game;
        this.name = name;
        this.description = description != null ? description : "";
        this.partyAt = partyAt;
        this.publicFlag = publicFlag;
        this.hit = 0;
        this.minimum = minimum;
        this.maximum = maximum;
        this.total = 0;
        this.partyStatus = PartyStatus.PENDING;
    }

    public void addPartyMember(PartyMember partyMember) {
        this.partyMembers.add(partyMember);
        partyMember.setParty(this);
        this.total += 1;
    }

    public void deletePartyMember(PartyMember partyMember) {
        this.partyMembers.remove(partyMember);
        partyMember.setParty(null);
        this.total -= 1;
    }

    public void update(PutPartyRequest request, SteamGame game) {
        this.name = request.name();
        this.description = request.description() != null ? request.description() : "";
        this.partyAt = request.partyAt();
        this.publicFlag = request.isPublic();
        this.minimum = request.minimum();
        this.maximum = request.maximum();
        this.game = game;
    }
}
