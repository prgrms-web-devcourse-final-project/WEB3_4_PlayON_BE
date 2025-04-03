package com.ll.playon.domain.game.game.dto;

import lombok.Data;

@Data
public class SteamCsvDto {
    private Long appid;
    private String name;
    private String releaseDate;
    private String headerImage;
    private Integer requiredAge;
    private String aboutTheGame;
    private String shortDescription;
    private String website;
    private String windows;
    private String mac;
    private String linux;
    private String categories;
    private String developers;
    private String publishers;
    private String screenshots;
    private String movies;
    private String genres;
    private Integer percentPositiveTotal;
    private Long totalReviewCount;
}
