package com.ll.playon.domain.member.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ll.playon.domain.member.entity.enums.Gender;
import com.ll.playon.domain.member.entity.enums.PlayStyle;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.entity.enums.SkillLevel;
import com.ll.playon.domain.title.entity.MemberStat;
import com.ll.playon.domain.title.entity.MemberTitle;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_member_steam_id", columnList = "steam_id"),
                @Index(name = "idx_member_username", columnList = "username"),
                @Index(name = "idx_member_role", columnList = "role")
        }
)
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime modifiedAt;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    @EqualsAndHashCode.Include
    private Long id;

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

    private String preferredGenre;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberTitle> memberTitles = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberStat> statItems = new ArrayList<>();

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

        if (isAdmin()) {
            authorities.add("ROLE_ADMIN");
        }

        return authorities;
    }

    private boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public void changeMemberId(Long newId) {
        setId(newId);
    }

    public void changeProfileImg(String url) {
        this.profileImg = url;
    }
}
