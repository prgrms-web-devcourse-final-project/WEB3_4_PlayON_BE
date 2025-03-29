package com.ll.playon.global.security;

import com.ll.playon.domain.member.MemberService;
import com.ll.playon.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
@RequiredArgsConstructor
public class UserContext {

    private final MemberService memberService;

    public Member getActor() {
        // TODO : 실제 인증된 사용자 조회로 바꾸기
        return memberService.findById(1L);
    }
}
