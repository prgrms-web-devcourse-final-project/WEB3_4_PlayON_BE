package com.ll.playon.domain.member.entity;

import com.ll.playon.domain.member.entity.enums.*;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTime {
    @Setter
    @Column(unique = true)
    private Long steamId;

    @Column(unique = true)
    private String username;

    private String nickname;

    private String password;

    private String profileImg;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

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
    @Builder.Default
    private String apiKey = UUID.randomUUID().toString();

    // TODO : 회원가입 확정 안되서 일단 기본값 처리
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PlayStyle playStyle = PlayStyle.CASUAL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SkillLevel skillLevel = SkillLevel.NEWBIE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Gender gender = Gender.MALE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PreferredGenres preferredGenres = PreferredGenres.RPG; // TODO : 게임 보유 목록 통해 설정

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

    public void changeMemberId(Long newId){
        setId(newId);
    }
}
