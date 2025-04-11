package com.ll.playon.global.webSocket.security;

import com.ll.playon.domain.member.service.AuthTokenService;
import com.ll.playon.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final AuthTokenService authTokenService;
    private final MemberService memberService;  // TODO: 배포 환경에서 삭제

    // TODO: 배포 환경에서 주석 해제 및 아래 메서드 삭제
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            Cookie[] cookies = servletRequest.getServletRequest().getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        String token = cookie.getValue();

                        if (validateToken(token, attributes)) {
                            return true;
                        }
                    }
                }
            }

            String token = servletRequest.getServletRequest().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                return validateToken(token, attributes);
            }
        }

        // 인증 실패 시 연결 거부
        return false;
    }

    private boolean validateToken(String token, Map<String, Object> attributes) {
        Map<String, Object> payload = this.authTokenService.payload(token);
        if (payload != null) {
            Long userId = ((Number) payload.get("id")).longValue();
            attributes.put("userId", userId);
            return true;
        }

        return false;
    }

//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//
//        // X-USER-ID 헤더에서 사용자 ID 추출
//        String userIdHeader = request.getHeaders().getFirst("X-USER-ID");
//
//        if (userIdHeader != null) {
//            try {
//                Long userId = Long.parseLong(userIdHeader);
//                Member user = memberService.findById(userId).get();
//
//                if (user != null) {
//                    // WebSocket 세션에 userId 저장
//                    attributes.put("userId", userId);
//
//                    // 임시로 Spring Security Context에 사용자 정보 설정 (테스트용)
//                    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                    return true;  // 인증 성공
//                }
//            } catch (NumberFormatException e) {
//                // X-USER-ID가 잘못된 값이라면 연결 거부
//                response.setStatusCode(HttpStatus.FORBIDDEN);  // 403 Forbidden
//                return false;
//            }
//        }
//
//        // X-USER-ID 헤더가 없다면 연결 거부
//        response.setStatusCode(HttpStatus.FORBIDDEN);  // 403 Forbidden
//        return false;
//    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {

    }
}
