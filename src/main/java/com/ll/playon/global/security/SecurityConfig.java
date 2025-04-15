package com.ll.playon.global.security;

import com.ll.playon.global.app.AppConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    public SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // 길드 부분
                                .requestMatchers(HttpMethod.GET, "/api/guilds/*/board").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/guilds/recommend").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/guilds/popular").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/guilds/list").permitAll()

                                // 파티, 파티로그 부분
                                .requestMatchers(HttpMethod.GET, "/api/parties/main/pending").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/parties/main/completed").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/parties/*").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/parties/*/result").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/parties/list").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/logs/party/*").permitAll()

                                // 사용자 부분
                                .requestMatchers(HttpMethod.POST, "/api/members/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/members/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/members/member/*").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/members/*/parties").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/members/*/parties/logs").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/auth/steam/signup").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/auth/steam/callback/signup").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/auth/steam/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/auth/steam/callback/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/steam/logout").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/members/{memberId:\\d+}/guilds").permitAll()

                                // 게임 부분
                                .requestMatchers(HttpMethod.GET, "/api/games/popular").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/games/ranking").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/games/recommend/playtime/top").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/games/list").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/games/search").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/games/*/party").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/games/*/logs").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/games/*/details").permitAll()

                                // 자유 게시판 부분
                                .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()

                                // 배치
                                .requestMatchers(HttpMethod.POST, "/api/batch/steam-game").permitAll()

                                // 나머지 api 요청들은 인증 필요
                                .requestMatchers("/api/**").authenticated()

                                // h2 콘솔, swagger 등 요청은 열림
                                .anyRequest().permitAll()
                )
                .headers(
                        headers ->
                                headers.frameOptions(
                                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                )
                )
                .csrf(
                        AbstractHttpConfigurer::disable
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
                AppConfig.getSiteFrontUrl()
        ));
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        // 자격 증명 허용 설정
        configuration.setAllowCredentials(true);
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));
        // CORS 설정을 소스에 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}