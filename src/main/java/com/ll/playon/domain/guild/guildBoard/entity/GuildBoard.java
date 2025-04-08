package com.ll.playon.domain.guild.guildBoard.entity;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
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
public class GuildBoard extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="guild_id", nullable = false)
    private Guild guild;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private GuildMember author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardTag tag;

    @Column(nullable = false)
    @Builder.Default
    private int hit=0;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount=0;

    private String imageUrl;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuildBoardComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuildBoardLike> likes = new ArrayList<>();


    public void increaseHit() {
        this.hit++;
    }

    public void increaseLike() {
        this.likeCount++;
    }

    public void decreaseLike() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void update(String title, String content, BoardTag tag, String imageUrl) {
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.imageUrl = imageUrl;
    }

    public void addComment(GuildBoardComment comment) {
        comments.add(comment);
        comment.setBoard(this);
    }

    public void addLike(GuildBoardLike like) {
        likes.add(like);
        like.setBoard(this);
    }
}
