package com.ll.playon.global.openFeign;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "steamOpenIdClient", url = "https://steamcommunity.com")
public interface SteamOpenIdClient {

    @PostMapping(
            value = "/openid/login",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    @Headers("Content-Type: application/x-www-form-urlencoded")
    String validateSteamId(String body);
}
