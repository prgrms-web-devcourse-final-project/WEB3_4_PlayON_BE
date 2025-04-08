package com.ll.playon.domain.guild.guildBoard.entity;

import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GuildBoardComment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="board_id", nullable = false)
    private GuildBoard board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private GuildMember author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    public void update(String newComment) {
        this.comment = newComment;
    }

    public void setBoard(GuildBoard board) {
        this.board = board;
        board.getComments().add(this);
    }
}