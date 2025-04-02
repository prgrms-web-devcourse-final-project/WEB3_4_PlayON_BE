package com.ll.playon.domain.guild.guild.entity;

import com.ll.playon.domain.guild.guild.dto.request.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.request.PutGuildRequest;
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

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuildTag> guildTags = new ArrayList<>();

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuildMember> members = new ArrayList<>();

    public void softDelete() {
        this.isDeleted = true;
        this.members.clear();
        this.name = "DELETED_" + UUID.randomUUID();
        this.description = "DELETED";
    }

    public void updateFromRequest(PutGuildRequest request) {
        this.name = request.name();
        this.description = request.description();
        this.maxMembers = request.maxMembers();
        this.isPublic = request.isPublic();
        this.guildImg = request.guildImg();
    }

    public static Guild createFrom(PostGuildRequest request, Member owner) {
        return Guild.builder()
                .owner(owner)
                .name(request.name())
                .description(request.description())
                .maxMembers(request.maxMembers())
                .isPublic(request.isPublic())
                .game(request.gameId())
                .guildImg(request.guildImg())
                .build();
    }
}

