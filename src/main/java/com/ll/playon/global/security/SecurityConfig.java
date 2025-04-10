package com.ll.playon.global.security;

import com.ll.playon.global.app.AppConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    public SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  //TODO: 제거
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                        "/api/guilds/*/board",
                                        "/api/guilds/recommend",
                                        "/api/guilds/popular",
                                        "/api/guilds/list",
                                        "/api/parties/main/pending",
                                        "/api/parties/main/completed",
                                        "/api/parties/*",
                                        "/api/parties/*/result",
                                        "/api/logs/party/*",
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
                                ).permitAll()

                                .requestMatchers("/api/**").authenticated()

                                .anyRequest().permitAll()
                )
                .headers(
                        headers ->
                                headers.frameOptions(
                                        frameOptions ->
                                                frameOptions.sameOrigin()
                                )
                )
                .csrf(
                        csrf ->
                                csrf.disable()
                )
                // 시큐리티 필터에 커스텀 필터 추가
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.getWriter().write(String.format(
                                            "{\"resultCode\": \"%d-1\", \"msg\": \"%s\", \"data\": null}",
                                            HttpServletResponse.SC_UNAUTHORIZED,
                                            "사용자 인증정보가 올바르지 않습니다."
                                    ));
                                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.getWriter().write(String.format(
                                            "{\"resultCode\": \"%d-1\", \"msg\": \"%s\", \"data\": null}",
                                            HttpServletResponse.SC_FORBIDDEN,
                                            "접근 권한이 없습니다."
                                    ));
                                })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 오리진 설정
        configuration.setAllowedOriginPatterns(Arrays.asList(
                AppConfig.getSiteFrontUrl(),
                "https://websocketking.com/"
        ));
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        // 자격 증명 허용 설정
        configuration.setAllowCredentials(true);
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // CORS 설정을 소스에 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}