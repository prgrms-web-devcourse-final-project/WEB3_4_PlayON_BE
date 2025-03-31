package com.ll.playon.global.steamAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SteamAPI {
    private final RestTemplate restTemplate;

    // TODO : 스팀 API 장애 대응

    @Value("${custom.steam.apikey}")
    private String apikey;

    public Map<String, String> getUserProfile(Long steamId) {
        String url = String.format("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=%d", apikey, steamId);

        String response = restTemplate.getForObject(url, String.class);

        System.out.println(response);

        Map<String, String> userProfile = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode playerNode = rootNode.path("response").path("players").get(0);

            String personaname = playerNode.path("personaname").asText();
            String avatarfull = playerNode.path("avatarfull").asText();

            userProfile.put("nickname", personaname);
            userProfile.put("profileImg", avatarfull);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userProfile;
    }

    public List<Long> getUserGames(Long steamId) {
        String url = String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%d", apikey, steamId);

        String response = restTemplate.getForObject(url, String.class);

        List<Long> gameIds = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode gamesNode = rootNode.path("response").path("games");

            // games 배열에서 appid만 추출하여 gameIds 리스트에 추가
            for (JsonNode gameNode : gamesNode) {
                gameIds.add(gameNode.path("appid").asLong());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gameIds;
    }

}
