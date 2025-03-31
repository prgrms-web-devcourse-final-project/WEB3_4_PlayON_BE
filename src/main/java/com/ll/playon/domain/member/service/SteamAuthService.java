package com.ll.playon.domain.member.service;

import com.ll.playon.domain.member.dto.SignupMemberDetailResponse;
import com.ll.playon.global.exceptions.ErrorCode;
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

    public SignupMemberDetailResponse validateSteamId(Map<String, String> params, String method) {
        String validationUrl = "https://steamcommunity.com/openid/login";
        String requestBody = buildValidationRequest(params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(validationUrl, HttpMethod.POST, request, String.class);

        if (response.getBody() != null && response.getBody().contains("is_valid:true")) {
            String steamId = extractSteamId(params.get("openid.claimed_id"));
            Long steamUserId = Long.valueOf(steamId);

            if(method.equals("login")) {
                memberService.steamLogin(steamUserId);
                return null;
            } else {
                return memberService.steamSignup(steamUserId);
            }
        } else {
            throw ErrorCode.AUTHORIZATION_FAILED.throwServiceException();
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
