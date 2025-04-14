package com.ll.playon.domain.guild.guildMember.entity;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GuildMember extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "guild_role", nullable = false)
    private GuildRole guildRole;

    public boolean isNotManagerOrLeader() {
        return guildRole != GuildRole.LEADER && guildRole != GuildRole.MANAGER;
    }

    public static GuildMember createLeader(Member owner, Guild guild) {
        return GuildMember.builder()
                .guild(guild)
                .member(owner)
                .guildRole(GuildRole.LEADER)
                .build();
        }
}
