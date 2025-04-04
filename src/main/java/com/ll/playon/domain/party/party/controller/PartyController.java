package com.ll.playon.domain.party.party.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.dto.request.GetAllPartiesRequest;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.request.PutPartyRequest;
import com.ll.playon.domain.party.party.dto.response.GetAllPendingMemberResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyDetailResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyMainResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.dto.response.PostPartyResponse;
import com.ll.playon.domain.party.party.dto.response.PutPartyResponse;
import com.ll.playon.domain.party.party.service.PartyService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.validation.GlobalValidation;
import com.ll.playon.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/parties")
@Tag(name = "PartyController")
public class PartyController {
    private final PartyService partyService;
    private final UserContext userContext;

    @PostMapping
    @Operation(summary = "파티 생성")
    public RsData<PostPartyResponse> createParty(@RequestBody @Valid PostPartyRequest postPartyRequest) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        return RsData.success(HttpStatus.CREATED, this.partyService.createParty(actor, postPartyRequest));
    }

    // TODO : 게임 쪽 나오면 게임 쪽 추가
    @GetMapping
    @Operation(summary = "조건 및 태그에 맞는 파티 리스트 조회")
    public RsData<PageDto<GetPartyResponse>> getAllParties(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "latest") String orderBy,
            @RequestParam(value = "partyAt", required = false)
            @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime partyAt,
            @RequestBody @Valid GetAllPartiesRequest getAllPartiesRequest
    ) {
        // TODO : 추후 롤백
//        정책 고민 (회원만 조회 가능하게 할 것인지)
//        Member actor = this.userContext.getActor();
        GlobalValidation.checkPageSize(pageSize);

        return RsData.success(HttpStatus.OK,
                new PageDto<>(this.partyService.getAllParties(page, pageSize, orderBy, partyAt, getAllPartiesRequest)));
    }

    @GetMapping("/main")
    @Operation(summary = "파티 메인용 리스트 조회")
    public RsData<GetPartyMainResponse> getPartyMain(@RequestParam(defaultValue = "2") int limit) {
        // TODO : 추후 롤백
//        정책 고민 (회원만 조회 가능하게 할 것인지)
//        Member actor = this.userContext.getActor();

        return RsData.success(HttpStatus.OK, this.partyService.getPartyMain(limit));
    }

    @GetMapping("/{partyId}")
    @Operation(summary = "파티 상세 정보 조회")
    public RsData<GetPartyDetailResponse> getPartyDetail(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        정책 고민 (회원만 조회 가능하게 할 것인지)
//        Member actor = this.userContext.getActor();

        return RsData.success(HttpStatus.OK, this.partyService.getPartyDetail(partyId));
    }

    @PutMapping("/{partyId}")
    @Operation(summary = "파티 수정")
    public RsData<PutPartyResponse> updateParty(@PathVariable long partyId,
                                                @RequestBody @Valid PutPartyRequest putPartyRequest) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        return RsData.success(HttpStatus.OK, this.partyService.updateParty(actor, partyId, putPartyRequest));
    }

    @DeleteMapping("/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 취소")
    public void deleteParty(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        this.partyService.deleteParty(actor, partyId);
    }

    @PostMapping("/{partyId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 신청")
    public void requestParticipation(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(6L);

        this.partyService.requestParticipation(actor, partyId);
    }

    @PutMapping("/{partyId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 신청 수락")
    public void approveParticipation(@PathVariable long partyId, @PathVariable long memberId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        this.partyService.approveParticipation(actor, partyId, memberId);
    }

    @DeleteMapping("/{partyId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 신청 거부")
    public void rejectParticipation(@PathVariable long partyId, @PathVariable long memberId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        this.partyService.rejectParticipation(actor, partyId, memberId);
    }

    @GetMapping("/{partyId}/pending")
    @Operation(summary = "파티 신청자 목록 확인")
    public RsData<GetAllPendingMemberResponse> getPartyPendingMembers(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        return RsData.success(HttpStatus.OK, this.partyService.getPartyPendingMembers(actor, partyId));
    }

    @PostMapping("/{partyId}/members/{memberId}/invitation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 초대")
    public void inviteParty(@PathVariable long partyId, @PathVariable long memberId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        this.partyService.inviteParty(actor, partyId, memberId);
    }
}
