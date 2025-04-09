package com.ll.playon.global.openFeign.dto.gameDetail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDetail {
    private String name;
    private int steam_appid;
    private int required_age;
    private String about_the_game;
    private String short_description;
    private String header_image;
    private String website;
    private List<Genre> genres;
    private List<Category> categories;
    private List<Screenshot> screenshots;
    private List<Movie> movies;
    private List<String> developers;
    private List<String> publishers;
    private ReleaseDate release_date;
    private Platform platforms;
}
