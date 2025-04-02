package com.ll.playon.domain.game.game.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SteamGame extends BaseTime {

    @Column(unique = true)
    private Long appid;

    @Column
    private String name;

    @Column(name = "header_image")
    private String headerImage;

    @Column
    private String genres; // JSON타입

    @Column(name = "required_age")
    private Integer requiredAge;

    @Column(name = "detailed_description", columnDefinition = "TEXT")
    private String detailedDescription;

    @Column(name = "about_the_game", columnDefinition = "TEXT")
    private String aboutTheGame;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "price_overview")
    private String priceOverview;

    @Column
    private String platforms; //mac, windows, linux boolean으로 넘어온거 처리 필요

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column
    private String website;

    @Column
    private Long recommendations;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteamImage> screenshots = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteamMovie> movies = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GameCategory> gameCategories = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GameTag> gameTags = new ArrayList<>();
}
