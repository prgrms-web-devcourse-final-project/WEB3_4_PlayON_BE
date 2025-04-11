package com.ll.playon.global.webSocket.security;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Long userId = (Long) attributes.get("userId");

        if (userId == null) {
            return null;
        }

        return new Principal() {
            @Override
            public String getName() {
                return userId.toString();
            }
        };
    }
}
