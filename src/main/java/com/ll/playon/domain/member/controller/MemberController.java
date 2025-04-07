package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.member.dto.*;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "MemberController")
public class MemberController {

    private final MemberService memberService;
    private final UserContext userContext;

    @PostMapping("/login")
    @Transactional
    @Operation(summary = "일반 회원 로그인")
    public RsData<String> loginNoSteam(@Valid @RequestBody MemberAuthRequest req) {
        memberService.loginNoSteam(req.username(), req.password());
        return RsData.success(HttpStatus.OK, "성공");
    }

    @PostMapping("/signup")
    @Transactional
    @Operation(summary = "일반 회원 회원가입")
    public RsData<MemberDetailDto> signupNoSteam(@Valid @RequestBody MemberAuthRequest req) {
        return RsData.success(HttpStatus.OK, memberService.signupNoSteam(req.username(), req.password()));
    }

    @PutMapping("/me")
    @Transactional
    @Operation(summary = "사용자 정보 수정")
    public RsData<String> modifyMember(@Valid @RequestBody MemberDetailDto req) {
        memberService.modifyMember(req, userContext.getActor());
        return RsData.success(HttpStatus.OK, "성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/me")
    @Transactional
    @Operation(summary = "회원 탈퇴")
    public RsData<String> deactivateMember() {
        memberService.deactivateMember(userContext.getActor());
        return RsData.success(HttpStatus.OK, "성공적으로 탈퇴되었습니다.");
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보")
    public RsData<MemberProfileResponse> getMyProfile() {
        return RsData.success(HttpStatus.OK, memberService.me(userContext.getActor()));
    }

    @GetMapping("/nickname")
    public RsData<List<GetMembersResponse>> getMembersByNickname(@RequestParam String nickname) {
        return RsData.success(HttpStatus.OK, memberService.findByNickname(nickname));
    }

    @GetMapping("/me/games")
    @Operation(summary = "사용자의 보유게임 조회")
    public RsData<List<GameListResponse>> getMembersByGame(@RequestParam(defaultValue = "3") int count) {
        return RsData.success(HttpStatus.OK, memberService.getOwnedGamesByMember(count, userContext.getActor()));
    }
}
