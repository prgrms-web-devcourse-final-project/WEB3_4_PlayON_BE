package com.ll.playon.domain.member.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.standard.util.Ut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Service
public class AuthTokenService {

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    @Value("${custom.accessToken.expirationSeconds}")
    private long accessTokenExpirationSeconds;

    String genAccessToken(Member user) {
        return Ut.jwt.toString(
                secretKey,
                accessTokenExpirationSeconds,
                Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole().name())
        );
    }

    public Map<String, Object> payload(String accessToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(secretKey, accessToken);

        if (ObjectUtils.isEmpty(parsedPayload)) return null;

        Role role;
        try {
            role = Role.valueOf((String) parsedPayload.get("role"));
        } catch (IllegalArgumentException e) {
            return null;
        }

        return Map.of("id", parsedPayload.get("id"), "username", parsedPayload.get("username"), "role", role);
    }
}