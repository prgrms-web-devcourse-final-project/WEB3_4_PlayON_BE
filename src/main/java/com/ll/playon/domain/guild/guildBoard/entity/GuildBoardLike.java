package com.ll.playon.domain.guild.guildBoard.entity;

import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"guild_member_id", "board_id"})
})
public class GuildBoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_member_id", nullable = false)
    private GuildMember guildMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private GuildBoard board;
}
