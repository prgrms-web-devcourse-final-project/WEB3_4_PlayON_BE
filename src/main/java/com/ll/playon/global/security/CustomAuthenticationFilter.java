package com.ll.playon.global.security;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final UserContext userContext;
    private final MemberService memberService;

    private static final List<String> PUBLIC_URLS = List.of(
            "/api/guilds/recommend",
            "/api/guilds/popular",
            "/api/guilds/list",

            "/api/parties/main/pending",
            "/api/parties/main/completed",
            "/api/parties/*/result",

            "/api/members/signup",
            "/api/members/login",
            "/api/auth/steam/signup",
            "/api/auth/steam/callback/signup",
            "/api/auth/steam/login",
            "/api/auth/steam/callback/login",
            "/api/auth/steam/logout",

            "/api/games/popular",
            "/api/games/ranking",
            "/api/games/recommend/playtime/top",
            "/api/games/list",
            "/api/games/search",
            "/api/games/*/party",
            "/api/games/*/logs",
            "/api/games/*/details",

            "/api/batch/steam-game"
    );
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();


    record AuthTokens(
            String apiKey,
            String accessToken
    ) { }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (!uri.startsWith("/api")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (PUBLIC_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri))) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthTokens authTokens = getAuthTokensFromRequest();

        if (authTokens == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = authTokens.apiKey;
        String accessToken = authTokens.accessToken;

        Member user = memberService.getUserFromAccessToken(accessToken);

        if (user == null)
            user = refreshAccessTokenByApiKey(apiKey);

        if (user != null)
            userContext.setLogin(user);

        filterChain.doFilter(request, response);
    }

    private Member refreshAccessTokenByApiKey(String apiKey) {
        Optional<Member> opUser = memberService.findByApiKey(apiKey);
        if (opUser.isEmpty()) return null;
        Member user = opUser.get();

        refreshAccessToken(user);
        return user;
    }

    private void refreshAccessToken(Member user) {
        String newAccessToken = memberService.genAccessToken(user);

        userContext.setHeader("Authorization", "Bearer " + user.getApiKey() + " " + newAccessToken);
        userContext.setCookie("accessToken", newAccessToken);
    }

    private AuthTokens getAuthTokensFromRequest() {
        // 요청 헤더에서 Authorization 얻기
        String authorization = userContext.getHeader("Authorization");

        // Authorization null 아니고 Bearer 시작하면 토큰값 얻기
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            String[] tokenBits = token.split(" ", 2);

            if (tokenBits.length == 2)
                return new AuthTokens(tokenBits[0], tokenBits[1]);
        }

        // 헤더에 토큰이 없다면 쿠키에서 토큰값 얻기
        String apikey = userContext.getCookieValue("apiKey");
        String accessToken = userContext.getCookieValue("accessToken");

        if (apikey != null && accessToken != null)
            return new AuthTokens(apikey, accessToken);

        return null;
    }
}