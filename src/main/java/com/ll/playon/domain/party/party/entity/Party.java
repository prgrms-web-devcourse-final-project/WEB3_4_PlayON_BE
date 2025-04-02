package com.ll.playon.domain.party.party.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Party extends BaseTime {
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

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyMember> partyMember = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyTag> partyTags = new ArrayList<>();

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
    }
}
