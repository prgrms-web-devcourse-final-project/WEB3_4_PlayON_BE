package com.ll.playon.global.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    // 필요에 따라 변경해서 쓰거나 삭제


//    private final MemberRepository memberRepository;
//    private Member actor;
//
//    public Member getActor() {
//        if (actor == null) {
//            actor = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
//                    .map(Authentication::getPrincipal)
//                    .filter(principal -> principal instanceof SecurityUser)
//                    .map(principal -> (SecurityUser) principal)
//                    .flatMap(securityUser -> memberRepository.findByMemberEmail(securityUser.getEmail()))
//                    .orElseThrow(ErrorCode.UNAUTHORIZED::throwServiceException);
//        }
//
//        return actor;
//    }
}
