package com.ll.playon.domain.game.game.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "steam_game", indexes = {
        @Index(name = "idx_game_appid", columnList = "appid"),
        @Index(name = "idx_game_name", columnList = "name"),
        @Index(name = "idx_game_genre", columnList = "id, genre_name")
})
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SteamGame extends BaseTime {

    @Column(unique = true)
    private Long appid;

    private String name;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "header_image")
    private String headerImage;

    @Column(name = "required_age")
    private Integer requiredAge;

    @Column(name = "about_the_game", columnDefinition = "TEXT")
    private String aboutTheGame;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    private String website;

    private boolean isWindowsSupported;
    private boolean isMacSupported;
    private boolean isLinuxSupported;

    private boolean isSinglePlayer;
    private boolean isMultiPlayer;

    @Column(columnDefinition = "TEXT")
    private String developers;

    @Column(columnDefinition = "TEXT")
    private String publishers;

    @Column(name = "pct_pos_total")
    private Integer percentPositiveTotal;

    @Column(name = "num_reviews_total")
    private Long totalReviewCount;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteamImage> screenshots = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteamMovie> movies = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "steam_game_genre",
            joinColumns = @JoinColumn(name = "steam_game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @Builder.Default
    private List<SteamGenre> genres = new ArrayList<>();

}
