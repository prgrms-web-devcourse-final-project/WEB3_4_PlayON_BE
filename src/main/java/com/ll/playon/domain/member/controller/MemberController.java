package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.member.dto.MemberAuthRequest;
import com.ll.playon.domain.member.dto.SignupMemberDetailResponse;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "MemberController")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    @Transactional
    @Operation(summary = "일반 회원 로그인")
    public RsData<String> loginNoSteam(@RequestBody MemberAuthRequest req) {
        memberService.loginNoSteam(req.username(), req.password());
        return RsData.success(HttpStatus.OK, "성공");
    }

    @PostMapping("/signup")
    @Transactional
    @Operation(summary = "일반 회원 회원가입")
    public RsData<SignupMemberDetailResponse> signupNoSteam(@RequestBody MemberAuthRequest req) {
        return RsData.success(HttpStatus.OK, memberService.signupNoSteam(req.username(), req.password()));
    }
}
