package com.ll.playon.domain.game.game.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "steam_game", indexes = {
        @Index(name = "idx_game_appid", columnList = "appid"),
        @Index(name = "idx_game_is_mac_supported", columnList = "is_mac_supported")
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

    @Column(name = "about_the_game", columnDefinition = "LONGTEXT")
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
            inverseJoinColumns = @JoinColumn(name = "genre_id"),
            indexes = {
                    @Index(name = "idx_genre_id_game_id", columnList = "genre_id, steam_game_id")
            })
    @Builder.Default
    private List<SteamGenre> genres = new ArrayList<>();

}
