package com.ll.playon.domain.party.partyLog.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.partyLog.dto.request.PostPartyLogRequest;
import com.ll.playon.domain.party.partyLog.dto.request.PutPartyLogRequest;
import com.ll.playon.domain.party.partyLog.dto.response.GetAllPartyLogResponse;
import com.ll.playon.domain.party.partyLog.dto.response.PartyLogResponse;
import com.ll.playon.domain.party.partyLog.service.PartyLogService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public RsData<PartyLogResponse> createPartyLog(@PathVariable long partyId,
                                                   @RequestBody @Valid PostPartyLogRequest request) {
        // TODO : 추후 롤백
        Member actor = this.userContext.getActualActor();

        return RsData.success(HttpStatus.CREATED, this.partyLogService.createPartyLog(actor, partyId, request));
    }

    @PostMapping("/{logId}/party/{partyId}/screenshot")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "스크린샷 URL 저장")
    public void saveImageUrl(@PathVariable long logId, @PathVariable long partyId, @RequestBody String url) {
        // TODO : 추후 롤백
        Member actor = this.userContext.getActualActor();

        this.partyLogService.saveImageUrl(actor, partyId, logId, url);
    }

    @GetMapping("/party/{partyId}")
    @Operation(summary = "파티의 모든 파티 로그 조회")
    public RsData<GetAllPartyLogResponse> getAllPartyLogs(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();

        return RsData.success(HttpStatus.OK, this.partyLogService.getAllPartyLogs(partyId));
    }

    @PutMapping("/{logId}/party/{partyId}")
    @Operation(summary = "파티 로그 수정")
    public RsData<PartyLogResponse> updatePartyLog(@PathVariable long logId,
                                                   @PathVariable long partyId,
                                                   @RequestBody @Valid PutPartyLogRequest request) {
        // TODO : 추후 롤백
        Member actor = this.userContext.getActualActor();

        return RsData.success(HttpStatus.OK, this.partyLogService.updatePartyLog(actor, partyId, logId, request));
    }

    @DeleteMapping("/{logId}/party/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 로그 삭제")
    public void deletePartyLog(@PathVariable long logId, @PathVariable long partyId) {
        // TODO : 추후 롤백
        Member actor = this.userContext.getActualActor();

        this.partyLogService.deletePartyLog(actor, partyId, logId);
    }
}
