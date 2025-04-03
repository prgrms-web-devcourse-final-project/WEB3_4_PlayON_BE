package com.ll.playon.domain.game.game.batch.reader;

import com.ll.playon.domain.game.game.dto.SteamCsvDto;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class SteamCsvReaderConfig {

    @Bean
    public FlatFileItemReader<SteamCsvDto> steamCsvReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setQuoteCharacter('"');
        tokenizer.setNames(
                "appid", "name", "release_date", "required_age", "price",
                "dlc_count", "detailed_description", "about_the_game", "short_description", "reviews",
                "header_image", "website", "support_url", "support_email",
                "windows", "mac", "linux", "metacritic_score", "metacritic_url",
                "achievements", "recommendations", "notes", "supported_languages",
                "full_audio_languages", "packages", "developers", "publishers",
                "categories", "genres", "screenshots", "movies",
                "user_score", "score_rank", "positive", "negative", "estimated_owners",
                "average_playtime_forever", "average_playtime_2weeks",
                "median_playtime_forever", "median_playtime_2weeks",
                "discount", "peak_ccu", "tags",
                "pct_pos_total", "num_reviews_total", "pct_pos_recent", "num_reviews_recent"
        );

        DefaultLineMapper<SteamCsvDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> {
            SteamCsvDto dto = new SteamCsvDto();
            dto.setAppid(fieldSet.readLong("appid"));
            dto.setName(fieldSet.readString("name"));
            dto.setReleaseDate(fieldSet.readString("release_date"));
            dto.setHeaderImage(fieldSet.readString("header_image"));
            dto.setRequiredAge(fieldSet.readInt("required_age"));
            dto.setAboutTheGame(fieldSet.readString("about_the_game"));
            dto.setShortDescription(fieldSet.readString("short_description"));
            dto.setWebsite(fieldSet.readString("website"));
            dto.setWindows(fieldSet.readString("windows"));
            dto.setMac(fieldSet.readString("mac"));
            dto.setLinux(fieldSet.readString("linux"));
            dto.setDevelopers(fieldSet.readString("developers"));
            dto.setPublishers(fieldSet.readString("publishers"));
            dto.setCategories(fieldSet.readString("categories"));
            dto.setGenres(fieldSet.readString("genres"));
            dto.setScreenshots(fieldSet.readString("screenshots"));
            dto.setMovies(fieldSet.readString("movies"));
            return dto;
        });

        FlatFileItemReader<SteamCsvDto> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("steam.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);

        return reader;
    }
}
