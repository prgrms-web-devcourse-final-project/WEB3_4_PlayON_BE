package com.ll.playon.domain.party.party.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.request.PutPartyRequest;
import com.ll.playon.domain.party.party.dto.response.GetPartyDetailResponse;
import com.ll.playon.domain.party.party.dto.response.PostPartyResponse;
import com.ll.playon.domain.party.party.dto.response.PutPartyResponse;
import com.ll.playon.domain.party.party.service.PartyService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
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
@RequestMapping("/api/parties")
public class PartyController {
    private final PartyService partyService;

    // TODO : 인증된 사용자로 변경
    private final UserContext userContext;

    @PostMapping
    public RsData<PostPartyResponse> createParty(@RequestBody @Valid PostPartyRequest postPartyRequest) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        return RsData.success(HttpStatus.CREATED, this.partyService.createParty(actor, postPartyRequest));
    }

    @GetMapping("/{partyId}")
    public RsData<GetPartyDetailResponse> getPartyDetail(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        정책 고민 (회원만 조회 가능하게 할 것인지)
//        Member actor = this.userContext.getActor();

        return RsData.success(HttpStatus.OK, this.partyService.getPartyDetail(partyId));
    }

    @PutMapping("/{partyId}")
    public RsData<PutPartyResponse> updateParty(@PathVariable long partyId,
                                                @RequestBody @Valid PutPartyRequest putPartyRequest) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        return RsData.success(HttpStatus.OK, this.partyService.updateParty(actor, partyId, putPartyRequest));
    }

    @DeleteMapping("/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParty(@PathVariable long partyId) {
        // TODO : 추후 롤백
//        Member actor = this.userContext.getActor();
        Member actor = this.userContext.findById(5L);

        this.partyService.deleteParty(actor, partyId);
    }
}
