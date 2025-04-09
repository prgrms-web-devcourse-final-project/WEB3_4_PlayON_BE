package com.ll.playon.domain.guild.guild.entity;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.guild.guild.dto.request.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.request.PutGuildRequest;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appid")
    private SteamGame game;

    @Column(name = "guild_img")
    private String guildImg;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuildTag> guildTags = new ArrayList<>();

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuildMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuildBoard> boards = new ArrayList<>();


    public void softDelete() {
        this.isDeleted = true;
        this.name = "DELETED_" + UUID.randomUUID();
        this.description = "DELETED";
        this.guildImg = "DELETED";
    }

    public void updateFromRequest(PutGuildRequest request, SteamGame game) {
        this.name = request.name();
        this.description = request.description();
        this.maxMembers = request.maxMembers();
        this.game = game;
        this.isPublic = request.isPublic();
    }

    public static Guild createFrom(PostGuildRequest request, Member owner, SteamGame game) {
        return Guild.builder()
                .owner(owner)
                .name(request.name())
                .description(request.description())
                .maxMembers(request.maxMembers())
                .isPublic(request.isPublic())
                .game(game)
                .build();
    }
}

