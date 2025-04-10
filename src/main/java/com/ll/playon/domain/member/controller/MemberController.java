package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.member.dto.*;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
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
    public RsData<PresignedUrlResponse> modifyMember(@Valid @RequestBody PutMemberDetailDto req) {
        return RsData.success(HttpStatus.OK, memberService.modifyMember(req, userContext.getActor()));
    }

    @PostMapping("/me/image")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "사용자 프로필 이미지 URL 저장")
    public void saveProfileImage(@RequestBody String url) {
        memberService.saveProfileImage(userContext.getActor(), url);
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
    @Operation(summary = "닉네임으로 사용자 리스트 조회")
    public RsData<List<GetMembersResponse>> getMembersByNickname(@RequestParam String nickname) {
        return RsData.success(HttpStatus.OK, memberService.findByNickname(nickname));
    }

    @GetMapping("/me/games")
    @Operation(summary = "사용자의 보유게임 조회")
    public RsData<List<GameListResponse>> getMembersByGame(@RequestParam(defaultValue = "3") int count) {
        return RsData.success(HttpStatus.OK, memberService.getOwnedGamesByMember(count, userContext.getActor()));
    }

    @PostMapping("/steamLink")
    @Operation(summary = "사용자의 보유게임 갱신")
    public RsData<String> linkSteamGames() {
        Member actor = userContext.getActor();
        if(ObjectUtils.isEmpty(actor)) throw ErrorCode.UNAUTHORIZED.throwServiceException();
        memberService.getUserGamesAndCheckGenres(actor);
        return RsData.success(HttpStatus.OK, "성공");
    }

    @PostMapping("/me/parties/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 초대 승락")
    public void approvePartyInvitation(@PathVariable long partyId) {
        Member actor = this.userContext.getActualActor();

        this.memberService.approvePartyInvitation(actor, partyId);
    }

    @DeleteMapping("/me/parties/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 초대 거절")
    public void rejectPartyInvitation(@PathVariable long partyId) {
        Member actor = this.userContext.getActualActor();

        this.memberService.rejectPartyInvitation(actor, partyId);
    }
}
