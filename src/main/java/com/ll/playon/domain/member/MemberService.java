package com.ll.playon.domain.member;

import com.ll.playon.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findById(Long id){
        // TODO : 예외 발생
        return memberRepository.findById(id).get();
    }
}
