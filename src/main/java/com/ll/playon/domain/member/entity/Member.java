package com.ll.playon.domain.member.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTime {
    @Column(unique = true, nullable = false)
    private Long steamId;

    @Column(unique = true, nullable = false)
    private String username;

    private String profile_img;

    @Column(nullable = false)
    @Builder.Default
    private boolean is_deleted = false;

    private LocalDateTime lastLoginAt;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Builder.Default
    private List<MemberSteamData> games = new ArrayList<>();

    // TODO : 회원가입 확정 안되서 일단 기본값 처리
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PlayStyle play_style = PlayStyle.CASUAL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Gender gender = Gender.MALE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PreferredGenres preferred_genres = PreferredGenres.RPG;
}
