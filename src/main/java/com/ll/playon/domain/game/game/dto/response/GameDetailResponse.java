package com.ll.playon.domain.game.game.dto.response;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.entity.SteamImage;
import com.ll.playon.domain.game.game.entity.SteamMovie;

import java.time.LocalDate;
import java.util.List;

public record GameDetailResponse(
        Long appid,
        String name,
        String headerImage,
        String shortDescription,
        String aboutTheGame,
        Integer requiredAge,
        String website,
        boolean isWindowsSupported,
        boolean isMacSupported,
        boolean isLinuxSupported,
        LocalDate releaseDate,
        String developers,
        String publishers,
        List<String> screenshots,
        List<String> movies,
        List<String> genres
) {
    public static GameDetailResponse from(SteamGame game) {
        return new GameDetailResponse(
                game.getAppid(),
                game.getName(),
                game.getHeaderImage(),
                game.getShortDescription(),
                game.getAboutTheGame(),
                game.getRequiredAge(),
                game.getWebsite(),
                game.isWindowsSupported(),
                game.isMacSupported(),
                game.isLinuxSupported(),
                game.getReleaseDate(),
                game.getDevelopers(),
                game.getPublishers(),
                game.getScreenshots().stream().map(SteamImage::getScreenshot).toList(),
                game.getMovies().stream().map(SteamMovie::getMovie).toList(),
                game.getGenres().stream().map(SteamGenre::getName).toList()
        );
    }
}

