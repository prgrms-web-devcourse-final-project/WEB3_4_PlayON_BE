package com.ll.playon.standard.util;

import com.ll.playon.standard.time.dto.TotalPlayTimeDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
public class Ut {
    public static class jwt {
        public static String toString(String secret, long expireSeconds, Map<String, ? extends Serializable> body) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            Date issuedAt = new Date();
            Date expiresAt = new Date(issuedAt.getTime() + expireSeconds * 1000);

            return Jwts.builder()
                    .setClaims(body)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expiresAt)
                    .signWith(secretKey)
                    .compact();
        }

        public static Map<String, Object> payload(String secret, String accessTokenStr) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            try {
                return Jwts
                        .parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(accessTokenStr)
                        .getBody();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class Time {
        public static TotalPlayTimeDto getTotalPlayTime(LocalDateTime startAt, LocalDateTime endAt) {
            Duration duration = Duration.between(startAt, endAt);

            return new TotalPlayTimeDto(
                    duration.toHours(),
                    duration.toMinutes() % 60,
                    duration.toSeconds() % 60
            );
        }
    }
}
