package com.ll.playon.domain.member;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.Role;
import com.ll.playon.standard.util.Ut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    @Value("${custom.accessToken.expirationSeconds}")
    private long accessTokenExpirationSeconds;

    String genAccessToken(Member user) {
        long id = user.getId();
        String username = user.getUsername();
        String role = user.getRole().name();

        return Ut.jwt.toString(
                secretKey,
                accessTokenExpirationSeconds,
                Map.of("id", id, "username", username, "role", role)
        );
    }

    public Map<String, Object> payload(String accessToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(secretKey, accessToken);

        if (parsedPayload == null) return null;

        long id = (long) (Integer) parsedPayload.get("id");
        String username = (String) parsedPayload.get("username");
        Role role = Role.valueOf((String) parsedPayload.get("role"));

        return Map.of("id", id, "username", username, "role", role);
    }
}