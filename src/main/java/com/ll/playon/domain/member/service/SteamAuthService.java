package com.ll.playon.domain.member.service;

import com.ll.playon.domain.member.controller.SteamRedirectPaths;
import com.ll.playon.domain.member.dto.MemberDetailDto;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.openFeign.SteamOpenIdClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SteamAuthService {
    private final MemberService memberService;
    private final SteamOpenIdClient steamOpenIdClient;

    @Transactional
    public MemberDetailDto validateSteamId(Map<String, String> params, String path, Member actor) {
        String requestBody = buildValidationRequest(params);
        String response = steamOpenIdClient.validateSteamId(requestBody);

        if (response != null && response.contains("is_valid:true")) {
            String steamId = extractSteamId(params.get("openid.claimed_id"));
            Long steamUserId = Long.valueOf(steamId);

            switch (path) {
                case SteamRedirectPaths.LOGIN -> {
                    memberService.steamLogin(steamUserId);
                    return null;
                }
                case SteamRedirectPaths.SIGNUP -> {
                    return memberService.steamSignup(steamUserId);
                }
                default -> {
                    memberService.steamLink(steamUserId, actor);
                    return null;
                }
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
