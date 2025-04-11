package com.ll.playon.domain.party.party.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.domain.party.party.dto.request.GetAllPartiesRequest;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.request.PutPartyRequest;
import com.ll.playon.domain.party.party.dto.response.GetAllPendingMemberResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyDetailResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyMainResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyResultResponse;
import com.ll.playon.domain.party.party.dto.response.PostPartyResponse;
import com.ll.playon.domain.party.party.dto.response.PutPartyResponse;
import com.ll.playon.domain.party.party.service.PartyService;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.service.TitleEvaluator;
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
    private final TitleEvaluator titleEvaluator;
    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "파티 생성")
    public RsData<PostPartyResponse> createParty(@RequestBody @Valid PostPartyRequest postPartyRequest) {
        Member actor = this.userContext.getActualActor();

        return RsData.success(HttpStatus.CREATED, this.partyService.createParty(actor, postPartyRequest));
    }

    @PostMapping("/list")
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
        Member actor = this.userContext.getActualActor();

        GlobalValidation.checkPageSize(pageSize);

        return RsData.success(HttpStatus.OK,
                new PageDto<>(this.partyService.getAllFilteredParties(actor, page, pageSize, orderBy, partyAt,
                        getAllPartiesRequest)));
    }

    @GetMapping("/{partyId}/result")
    @Operation(summary = "파티 결과 조회")
    public RsData<GetPartyResultResponse> getPartyResult(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        정책 고민 (회원만 조회 가능하게 할 것인지)
//        Member actor = this.userContext.getActor();
//        Member actor = this.userContext.findById(5L);

        return RsData.success(HttpStatus.OK, this.partyService.getPartyResult(partyId));
    }

    @GetMapping("/main/pending")
    @Operation(summary = "메인용 진행 예정 리스트 조회")
    public RsData<GetPartyMainResponse> getPendingPartyMain(@RequestParam(defaultValue = "2") int limit) {
        // TODO : 추후 롤백
//        정책 고민 (회원만 조회 가능하게 할 것인지)
//        Member actor = this.userContext.getActor();
//        Member actor = this.userContext.findById(5L);

        return RsData.success(HttpStatus.OK, this.partyService.getPendingPartyMain(limit));
    }

    @GetMapping("/main/completed")
    @Operation(summary = "메인용 파티 로그가 작성되었고 종료된 파티 리스트 조회")
    public RsData<GetPartyMainResponse> getCompletedPartyWithLogMain(@RequestParam(defaultValue = "3") int limit) {
        // TODO : 추후 롤백
//        정책 고민 (회원만 조회 가능하게 할 것인지)
//        Member actor = this.userContext.getActor();

        return RsData.success(HttpStatus.OK, this.partyService.getCompletedPartyWithLogMain(limit));
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
        Member actor = this.userContext.getActualActor();

        return RsData.success(HttpStatus.OK, this.partyService.updateParty(actor, partyId, putPartyRequest));
    }

    @DeleteMapping("/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 취소")
    public void deleteParty(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.getActualActor();

        this.partyService.deleteParty(actor, partyId);
    }

    @PostMapping("/{partyId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 신청")
    public void requestParticipation(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.getActualActor();

        this.partyService.requestParticipation(actor, partyId);
    }

    @PutMapping("/{partyId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 신청 수락")
    public void approveParticipation(@PathVariable long partyId, @PathVariable long memberId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.getActualActor();

        this.partyService.approveParticipation(actor, partyId, memberId);

        // 파티 참여 칭호
        titleEvaluator.check(ConditionType.PARTY_JOIN_COUNT, memberService.findById(memberId).get());
    }

    @DeleteMapping("/{partyId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 신청 거부")
    public void rejectParticipation(@PathVariable long partyId, @PathVariable long memberId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.getActualActor();

        this.partyService.rejectParticipation(actor, partyId, memberId);
    }

    @GetMapping("/{partyId}/pending")
    @Operation(summary = "파티 신청자 목록 확인")
    public RsData<GetAllPendingMemberResponse> getPartyPendingMembers(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.getActualActor();

        return RsData.success(HttpStatus.OK, this.partyService.getPartyPendingMembers(actor, partyId));
    }

    @PostMapping("/{partyId}/members/{memberId}/invitation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 초대")
    public void inviteParty(@PathVariable long partyId, @PathVariable long memberId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.getActualActor();

        this.partyService.inviteParty(actor, partyId, memberId);
    }
}
