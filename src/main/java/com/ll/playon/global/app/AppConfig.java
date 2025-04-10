package com.ll.playon.global.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix= "app")
public class AppConfig {

    @Getter
    @Setter
    private String mode;

    @Setter
    private String siteFrontUrl;  // 설정에서 주입받는 값

    private static String staticSiteFrontUrl;

    @Getter
    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AppConfig.objectMapper = objectMapper;
    }

    // siteFrontUrl을 정적 필드에 복사
    @PostConstruct
    public void init() {
        AppConfig.staticSiteFrontUrl = this.siteFrontUrl;
    }

    // 그대로 유지
    public static String getSiteFrontUrl() {
        return staticSiteFrontUrl != null ? staticSiteFrontUrl : "http://localhost:3000";
    }
}
