package com.ll.playon.global.openFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "steamOpenIdClient", url = "https://steamcommunity.com")
public interface SteamOpenIdClient {

    @PostMapping(value = "/openid/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String validateSteamId(@RequestBody String requestBody);
}
