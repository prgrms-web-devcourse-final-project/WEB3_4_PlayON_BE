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

    @Enumerated(EnumType.STRING)
    @Column(name = "guild_role", nullable = false)
    private GuildRole guildRole;

    public void setGuildRole(GuildRole guildRole) {
        this.guildRole = guildRole;
    }

    public boolean isManagerOrLeader() {
        return guildRole == GuildRole.LEADER || guildRole == GuildRole.MANAGER;
    }
}
