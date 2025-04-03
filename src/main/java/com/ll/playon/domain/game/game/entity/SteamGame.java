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
        @Index(name = "idx_game_name", columnList = "name")
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

    private LocalDate releaseDate;

    private String headerImage;

    private Integer requiredAge;

    @Column(columnDefinition = "TEXT")
    private String aboutTheGame;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    private String website;

    private boolean isWindowsSupported;
    private boolean isMacSupported;
    private boolean isLinuxSupported;

    private String developers;

    private String publishers;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteamImage> screenshots = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteamMovie> movies = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteamGenre> steamGenres = new ArrayList<>();
}
