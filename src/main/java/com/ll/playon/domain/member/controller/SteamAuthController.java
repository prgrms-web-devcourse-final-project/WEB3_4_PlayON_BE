package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.member.dto.MemberDetailDto;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.SteamAuthService;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/steam")
@RequiredArgsConstructor
@Tag(name = "SteamAuthController")
public class SteamAuthController {
    private final UserContext userContext;
    private final SteamAuthService steamAuthService;

    private static final String REALM_URL = "http://localhost:8080"; // TODO : 배포시 해당 도메인으로 수정
    private static final String STEAM_OPENID_URL = "https://steamcommunity.com/openid/login";
    private static final String RETURN_URL = REALM_URL +"/api/auth/steam/callback";

    @GetMapping("/login")
    @Operation(summary = "로그인 : 스팀 로그인 리다이렉트")
    public RsData<Map<String, String>> loginRedirectToSteam() {
        return redirectToSteam(SteamRedirectPaths.LOGIN);
    }
    @GetMapping("/signup")
    @Operation(summary = "회원가입 : 스팀 로그인 리다이렉트")
    public RsData<Map<String, String>> signupRedirectToSteam() {
        return redirectToSteam(SteamRedirectPaths.SIGNUP);
    }
    @GetMapping("/link")
    @Operation(summary = "계정 연동 : 스팀 로그인 리다이렉트")
    public RsData<Map<String, String>> linkRedirectToSteam() {
        return redirectToSteam(SteamRedirectPaths.LINK);
    }
    private RsData<Map<String, String>> redirectToSteam(String url) {
        final String authUrl = STEAM_OPENID_URL + "?openid.ns=http://specs.openid.net/auth/2.0"
                + "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
                + "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select"
                + "&openid.return_to=" + RETURN_URL + url
                + "&openid.realm=" + REALM_URL
                + "&openid.mode=checkid_setup";

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", authUrl);

        return RsData.success(HttpStatus.OK, response);
    }


    @GetMapping("/callback/login")
    @Operation(summary = "스팀 인증 검증 및 로그인")
    public RsData<String> loginHandleSteamCallback(@RequestParam Map<String, String> params) {
        handleSteamCallbackWithoutActor(params, SteamRedirectPaths.LOGIN);
        return RsData.success(HttpStatus.OK, "성공");
    }
    @GetMapping("/callback/signup")
    @Operation(summary = "스팀 인증 검증 및 회원가입")
    public RsData<MemberDetailDto> signupHandleSteamCallback(@RequestParam Map<String, String> params) {
        return RsData.success(HttpStatus.OK, handleSteamCallbackWithoutActor(params, SteamRedirectPaths.SIGNUP));
    }
    @GetMapping("/callback/link")
    @Operation(summary = "스팀 인증 검증 및 계정 연동")
    public RsData<String> linkHandleSteamCallback(@RequestParam Map<String, String> params) {
        handleSteamCallbackWithActor(params, SteamRedirectPaths.LINK);
        return RsData.success(HttpStatus.OK, "성공");
    }

    public MemberDetailDto handleSteamCallbackWithoutActor(Map<String, String> params, String path) {
        return handleSteamCallback(params, path, null);
    }
    public void handleSteamCallbackWithActor(Map<String, String> params, String path) {
        handleSteamCallback(params, path, userContext.getActor());
    }

    public MemberDetailDto handleSteamCallback(@RequestParam Map<String, String> params, String path, Member actor) {
        if (!"id_res".equals(params.get("openid.mode"))) {
            throw ErrorCode.EXTERNAL_API_UNEXPECTED_REQUEST.throwServiceException();
        }
        return steamAuthService.validateSteamId(params, path, actor);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "로그아웃")
    public void logout() {
        userContext.deleteCookie("accessToken");
        userContext.deleteCookie("apiKey");
        SecurityContextHolder.clearContext();
    }
}