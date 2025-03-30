package com.ll.playon.domain.member.entity;

import com.ll.playon.domain.member.entity.enums.Gender;
import com.ll.playon.domain.member.entity.enums.PlayStyle;
import com.ll.playon.domain.member.entity.enums.PreferredGenres;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

    @Setter
    private LocalDateTime lastLoginAt;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Builder.Default
    private List<MemberSteamData> games = new ArrayList<>();

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String apiKey;

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

    public Member(long id, String username, Role role) {
        super();
        this.setId(id);
        this.username = username;
        this.role = role;
    }

    public Member(Long id, String username) {
        super();
        this.setId(id);
        this.username = username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsString()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private List<String> getAuthoritiesAsString() {
        List<String> authorities = new ArrayList<>();

        if (isAdmin()) authorities.add("ROLE_ADMIN");

        return authorities;
    }

    private boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
}
