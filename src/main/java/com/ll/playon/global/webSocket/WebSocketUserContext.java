package com.ll.playon.global.webSocket;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.global.exceptions.ErrorCode;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketUserContext {
    private final MemberService memberService;

    public Member getActor(Principal principal) {
        return Optional.ofNullable(principal)
                .map(Principal::getName)
                .map(Long::parseLong)
                .flatMap(memberService::findById)
                .orElseThrow(ErrorCode.USER_NOT_REGISTERED::throwServiceException);
    }

    public Member findById(Long id) {
        return this.memberService.findById(id)
                .orElseThrow(ErrorCode.USER_NOT_REGISTERED::throwServiceException);
    }
}
