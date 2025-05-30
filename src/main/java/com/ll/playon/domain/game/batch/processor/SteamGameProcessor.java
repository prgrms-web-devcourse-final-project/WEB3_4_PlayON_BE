package com.ll.playon.domain.game.batch.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.playon.domain.game.game.dto.SteamCsvDto;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.entity.SteamImage;
import com.ll.playon.domain.game.game.entity.SteamMovie;
import com.ll.playon.domain.game.game.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SteamGameProcessor implements ItemProcessor<SteamCsvDto, SteamGame> {

    private final ObjectMapper objectMapper;
    private final GenreRepository genreRepository;

    @Override
    public SteamGame process(SteamCsvDto dto) throws Exception {

        List<SteamGenre> genreEntities = new ArrayList<>();
        try {
            String rawGenres = dto.getGenres();
            if (StringUtils.hasText(rawGenres)) {
                String fixedJson = rawGenres.replace("'", "\"");
                List<String> genreNames = objectMapper.readValue(fixedJson, new TypeReference<>() {});
                genreEntities = genreNames.stream()
                        .map(genreRepository::findOrCreate)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("장르 파싱 실패: {}", dto.getGenres());
            throw e;
        }

        List<SteamImage> screenshotEntities = new ArrayList<>();
        if (StringUtils.hasText(dto.getScreenshots())) {
            try {
                String fixedJson = dto.getScreenshots().replace("'", "\"");
                List<String> screenshotUrls = objectMapper.readValue(fixedJson, new TypeReference<>() {});
                screenshotEntities = screenshotUrls.stream()
                        .map(url -> SteamImage.builder().screenshot(url).build())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("스크린샷 파싱 실패: {}", dto.getScreenshots());
            }
        }

        List<SteamMovie> movieEntities = new ArrayList<>();
        if (StringUtils.hasText(dto.getMovies())) {
            try {
                String fixedJson = dto.getMovies().replace("'", "\"");
                List<String> movieUrls = objectMapper.readValue(fixedJson, new TypeReference<>() {});
                movieEntities = movieUrls.stream()
                        .map(url -> SteamMovie.builder().movie(url).build())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("무비 파싱 실패: {}", dto.getMovies());
            }
        }

        boolean isWindows = Boolean.parseBoolean(dto.getWindows());
        boolean isMac = Boolean.parseBoolean(dto.getMac());
        boolean isLinux = Boolean.parseBoolean(dto.getLinux());

        boolean isSinglePlayer = false;
        boolean isMultiPlayer = false;

        try {
            String fixedJson = dto.getCategories().replace("'", "\"");
            List<String> categories = objectMapper.readValue(fixedJson, new TypeReference<>() {});

            isSinglePlayer = categories.stream()
                    .anyMatch(c -> c.equalsIgnoreCase("Single-player"));
            isMultiPlayer = categories.stream()
                    .anyMatch(c -> c.equalsIgnoreCase("Multi-player"));

        } catch (Exception e) {
            log.error("카테고리 파싱 실패: {}", dto.getCategories());
        }

        String developers = dto.getDevelopers();
        if (StringUtils.hasText(developers)) {
            try {
                developers = developers.replace("'", "\"");
                List<String> developerList = objectMapper.readValue(developers, new TypeReference<>() {});
                developers = developerList.isEmpty() ? null : developerList.getFirst();
            } catch (Exception e) {
                log.error("developers 파싱 실패: {}", developers);
                developers = null;
            }
        }

        String publishers = dto.getPublishers();
        if (StringUtils.hasText(publishers)) {
            try {
                publishers = publishers.replace("'", "\"");
                List<String> publisherList = objectMapper.readValue(publishers, new TypeReference<>() {});
                publishers = publisherList.isEmpty() ? null : publisherList.getFirst();
            } catch (Exception e) {
                log.error("publishers 파싱 실패: {}", publishers);
                publishers = null;
            }
        }

        int requiredAge = 0;
        if (dto.getRequiredAge() >= 18) {
            requiredAge = 18;
        } else if (dto.getRequiredAge() >= 15) {
            requiredAge = 15;
        }

        SteamGame game = SteamGame.builder()
                .appid(dto.getAppid())
                .name(dto.getName())
                .releaseDate(LocalDate.parse(dto.getReleaseDate()))
                .headerImage(dto.getHeaderImage())
                .requiredAge(requiredAge)
                .aboutTheGame(dto.getAboutTheGame())
                .shortDescription(dto.getShortDescription())
                .website(dto.getWebsite())
                .isWindowsSupported(isWindows)
                .isMacSupported(isMac)
                .isLinuxSupported(isLinux)
                .isSinglePlayer(isSinglePlayer)
                .isMultiPlayer(isMultiPlayer)
                .developers(developers)
                .publishers(publishers)
                .genres(genreEntities)
                .percentPositiveTotal(dto.getPercentPositiveTotal())
                .totalReviewCount(dto.getTotalReviewCount())
                .build();

        screenshotEntities.forEach(img -> img.setGame(game));
        movieEntities.forEach(mv -> mv.setGame(game));
        game.setScreenshots(screenshotEntities);
        game.setMovies(movieEntities);

        return game;
    }
}
