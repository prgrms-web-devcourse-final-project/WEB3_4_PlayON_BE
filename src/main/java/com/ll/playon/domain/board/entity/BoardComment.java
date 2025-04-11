package com.ll.playon.domain.board.entity;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardComment extends BaseTime {

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    public void updateComment(String comment) {
        this.comment = comment;
    }
}
