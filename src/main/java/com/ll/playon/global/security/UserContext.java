package com.ll.playon.global.security;

import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Optional;

@RequestScope
@Component
@RequiredArgsConstructor
public class UserContext {

    private final MemberService memberService;
    private final HttpServletResponse resp;
    private final HttpServletRequest req;

    private static final String COOKIE_DOMAIN = "localhost";

    // 쿠키 생성
    public void setCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain(COOKIE_DOMAIN)
                .httpOnly(true)
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }

    // 요청에서 헤더 얻어오기
    public String getHeader(String name) {
        return req.getHeader(name);
    }

    // 쿠키 값 얻기
    public String getCookieValue(String name) {
        return Optional
                .ofNullable(req.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    // 응답 헤더 설정
    public void setHeader(String name, String value) {
        resp.setHeader(name, value);
    }

    // 로그인 설정
    public void setLogin(Member user) {
        UserDetails userDetails = new SecurityUser(
                user.getId(),
                user.getUsername(),
                user.getAuthorities()
        );

        // 인증
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // 인증 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 쿠키 삭제
    public void deleteCookie(String name) {
        ResponseCookie cookie = ResponseCookie.from(name, null)
                .path("/")
                .domain("localhost")
                .httpOnly(true)
                .maxAge(0)
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }

    // JWT 생성하고 쿠키 생성
    public String makeAuthCookies(Member user) {
        String accessToken = memberService.genAccessToken(user);

        setCookie("apiKey", user.getApiKey());
        setCookie("accessToken", accessToken);

        return accessToken;
    }

    public Member getActor() {
        // TODO : 실제 인증된 사용자 조회로 바꾸기
        return memberService.findById(1L).get();
    }

//    // 요청을 보낸 사용자의 인증 정보를 가져와 해당 사용자를 조회, 시큐리티 내부에 인증된 사용자를 반환
//    public Member getActor() {
//        return Optional.ofNullable(
//                        SecurityContextHolder
//                                .getContext()
//                                .getAuthentication()
//                )
//                .map(Authentication::getPrincipal)
//                .filter(principal -> principal instanceof SecurityUser)
//                .map(principal -> (SecurityUser) principal)
//                .map(securityUser -> new Member(securityUser.getId(), securityUser.getUsername()))
//                .orElse(null);
//    }
//
//    // 요청을 보낸 사용자의 인증 정보를 가져와 실제 DB에 저장된 user 찾기
//    public Optional<Member> findActor() {
//        Member actor = getActor();
//
//        if (actor == null) {
//            return Optional.empty();
//        }
//
//        return memberService.findById(actor.getId());
//    }
}
