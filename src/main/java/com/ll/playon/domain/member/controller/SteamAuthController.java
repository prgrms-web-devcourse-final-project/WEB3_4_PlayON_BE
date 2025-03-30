package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/steam")
@RequiredArgsConstructor
public class SteamAuthController {
    private final RestTemplate restTemplate;
    private final MemberService memberService;
    private final UserContext userContext;

    private static final String REALM_URL = "http://localhost:8080"; // TODO : 배포시 해당 도메인으로 수정
    private static final String STEAM_OPENID_URL = "https://steamcommunity.com/openid/login";
    private static final String RETURN_URL = REALM_URL +"/api/auth/steam/callback";

    @GetMapping
    public ResponseEntity<Map<String, String>> redirectToSteam() {
        String authUrl = STEAM_OPENID_URL + "?openid.ns=http://specs.openid.net/auth/2.0"
                + "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
                + "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select"
                + "&openid.return_to=" + RETURN_URL
                + "&openid.realm=" + REALM_URL
                + "&openid.mode=checkid_setup";

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", authUrl);

        return ResponseEntity.ok(response); // TODO : 응답 형식에 맞게 수정
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleSteamCallback(@RequestParam Map<String, String> params) {
        if (!params.containsKey("openid.mode") || !params.get("openid.mode").equals("id_res")) {
            return ResponseEntity.badRequest().body("Invalid OpenID response"); // TODO : 예외 형식에 맞게 수정
        }

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

            return ResponseEntity.ok("Steam OpenID authentication successful!"); // TODO : 응답 형식에 맞게 수정
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Steam OpenID authentication failed."); // TODO : 예외 형식에 맞게 수정
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

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<Void> logout() {

        userContext.deleteCookie("accessToken");
        userContext.deleteCookie("apiKey");
        SecurityContextHolder.clearContext();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}