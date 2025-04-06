package com.ll.playon.global.openFeign.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SteamSearchResponse {
    private String desc;
    private List<GameItem> items;
}
