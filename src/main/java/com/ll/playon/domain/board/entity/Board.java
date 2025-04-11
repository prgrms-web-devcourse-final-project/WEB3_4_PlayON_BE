package com.ll.playon.domain.board.entity;

import com.ll.playon.domain.board.enums.BoardCategory;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Board extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory category;

    @Column(nullable = false)
    @Builder.Default
    private int hit = 0;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BoardComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BoardLike> likes = new ArrayList<>();

    public void increaseHit() {
        this.hit++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void update(String title, String content, BoardCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void addComment(BoardComment comment) {
        this.comments.add(comment);
    }
}
