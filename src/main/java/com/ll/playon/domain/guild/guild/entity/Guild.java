package com.ll.playon.domain.guild.guild.entity;

import com.ll.playon.domain.guild.guild.enums.*;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Guild extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_members", nullable = false)
    private int maxMembers;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    // TODO: 게임 생성후 연결
    @Column(name = "game_id")
//    @ManyToOne(fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "game_id")
    private Long game; // 임시로 게임 id를 Long 설정해놓음

    @Column(name = "guild_img")
    private String guildImg;

    @Enumerated(EnumType.STRING)
    @Column(name = "party_style", nullable = false)
    private PartyStyle partyStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_skill", nullable = false)
    private GameSkill gameSkill;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_filter", nullable = false)
    private GenderFilter genderFilter;

    @Enumerated(EnumType.STRING)
    @Column(name = "active_time", nullable = false)
    private ActiveTime activeTime;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuildMember> members = new ArrayList<>();

    public void softDelete() {
        this.isDeleted = true;
        this.members.clear();
        this.name = "DELETED_" + UUID.randomUUID();
        this.description = "DELETED";
    }
}
