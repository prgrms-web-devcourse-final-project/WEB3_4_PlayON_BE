package com.ll.playon.standard.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtil {
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie); // 쿠키가 존재하면 반환
                }
            }
        }

        return Optional.empty(); // 없으면 빈값 반환
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // 자바스크립트에서 쿠키에 접근할 수 없도록 설정 (XSS 방지)
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue(""); // 쿠키의 값을 빈 문자열로 설정
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 쿠키의 유효시간을 0으로 설정하여 쿠키 삭제
                    response.addCookie(cookie);
                }
            }
        }
    }
} 