package com.ll.playon.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SteamAuthService {
    private final RestTemplate restTemplate;
    private final MemberService memberService;

    public boolean validateSteamId(Map<String, String> params) {
        String validationUrl = "https://steamcommunity.com/openid/login";
        String requestBody = buildValidationRequest(params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(validationUrl, HttpMethod.POST, request, String.class);

        if (response.getBody() != null && response.getBody().contains("is_valid:true")) {
            String steamId = extractSteamId(params.get("openid.claimed_id"));

            System.out.println("[Steam OpenID] User ID: " + steamId); // 스팀 아이디 출력
            memberService.signupOrSignin(steamId);

            return true;
        } else {
            return false;
        }
    }

    private String buildValidationRequest(Map<String, String> params) {
        params.put("openid.mode", "check_authentication");

        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    private String extractSteamId(String claimedId) {
        if (claimedId != null && claimedId.matches(".*/id/\\d+$")) {
            return claimedId.substring(claimedId.lastIndexOf("/") + 1);
        }
        return "Unknown";
    }
}
