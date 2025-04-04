package com.ll.playon.domain.party.partyLog.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.partyLog.dto.request.PostPartyLogRequest;
import com.ll.playon.domain.party.partyLog.dto.response.PostPartyLogResponse;
import com.ll.playon.domain.party.partyLog.service.PartyLogService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
@Tag(name = "PartyLogController")
public class PartyLogController {
    private final PartyLogService partyLogService;
    private final UserContext userContext;

    @PostMapping("/party/{partyId}")
    @Operation(summary = "파티 로그 작성")
    public RsData<PostPartyLogResponse> createPartyLog(@PathVariable long partyId,
                                                       @RequestBody @Valid PostPartyLogRequest request) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        return RsData.success(HttpStatus.CREATED, this.partyLogService.createPartyLog(actor, partyId, request));
    }

    @PostMapping("/{logId}/party/{partyId}/screenshot")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "스크린샷 URL 저장")
    public void saveImageUrl(@PathVariable long logId, @PathVariable long partyId, @RequestBody String url) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        this.partyLogService.saveImageUrl(actor, partyId, logId, url);
    }
}
