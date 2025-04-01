package com.ll.playon.domain.party.party.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.dto.response.PostPartyResponse;
import com.ll.playon.domain.party.party.service.PartyService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plays")
public class PartyController {
    private final PartyService partyService;

    // TODO : 인증된 사용자로 변경
    private final UserContext userContext;

    @PostMapping
    public RsData<PostPartyResponse> createParty(@RequestBody @Valid PostPartyRequest postPartyRequest) {
        // TODO : 사용할지 말지 추후에 결정
        Member actor = this.userContext.getActor();

        return RsData.success(HttpStatus.CREATED, this.partyService.createParty(postPartyRequest));
    }
}
