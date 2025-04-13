package com.ll.playon.domain.member.controller;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.guild.guild.dto.request.PostImageUrlRequest;
import com.ll.playon.domain.guild.guild.dto.response.GetGuildListResponse;
import com.ll.playon.domain.guild.guildMember.service.GuildMemberService;
import com.ll.playon.domain.member.dto.*;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.domain.member.service.SteamAsyncService;
import com.ll.playon.domain.party.party.dto.response.GetPartyMainResponse;
import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.standard.page.dto.PageDto;
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
    private final SteamAsyncService steamAsyncService;
    private final UserContext userContext;
    private final GuildMemberService guildMemberService;

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
    @Operation(summary = "내 정보 수정")
    public RsData<PresignedUrlResponse> modifyMember(@Valid @RequestBody PutMemberDetailDto req) {
        return RsData.success(HttpStatus.OK, memberService.modifyMember(req, userContext.getActor()));
    }

    @PostMapping("/me/image")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "내 프로필 이미지 URL 저장")
    public void saveProfileImage(@RequestBody PostImageUrlRequest url) {
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

    @GetMapping("/member/{memberId}")
    @Operation(summary = "다른 회원 정보")
    public RsData<MemberProfileResponse> getMemberProfile(@PathVariable Long memberId) {
        return RsData.success(HttpStatus.OK,
                memberService.me(memberService.findById(memberId)
                        .orElseThrow(ErrorCode.USER_NOT_FOUND::throwServiceException)));
    }

    @GetMapping("/nickname")
    @Operation(summary = "닉네임으로 회원 명단 조회")
    public RsData<List<GetMembersResponse>> getMembersByNickname(@RequestParam String nickname) {
        return RsData.success(HttpStatus.OK, memberService.findByNickname(nickname));
    }

    @GetMapping("/me/games")
    @Operation(summary = "나의 보유게임 조회")
    public RsData<List<GameListResponse>> getMembersByGame(@RequestParam(defaultValue = "3") int count) {
        return RsData.success(HttpStatus.OK, memberService.getOwnedGamesByMember(count, userContext.getActor()));
    }

    @PostMapping("/steamLink")
    @Operation(summary = "나의 보유게임 갱신")
    public RsData<String> linkSteamGames() {
        Member actor = userContext.getActor();
        if (ObjectUtils.isEmpty(actor)) {
            throw ErrorCode.UNAUTHORIZED.throwServiceException();
        }
        steamAsyncService.getUserGamesAndCheckGenres(actor);
        return RsData.success(HttpStatus.OK, "성공");
    }

    @DeleteMapping("/me/parties/pending/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 신청 취소")
    public void cancelPendingParty(@PathVariable long partyId) {
        Member actor = this.userContext.getActualActor();

        this.memberService.cancelPendingParty(actor, partyId);
    }

    @PutMapping("/me/parties/{partyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "파티 초대 승낙")
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

    @GetMapping("/me/parties")
    @Operation(summary = "나의 참여중인 파티 조회")
    public RsData<GetPartyMainResponse> getMyParties() {
        Member actor = this.userContext.getActor();

        return RsData.success(HttpStatus.OK, this.memberService.getMyParties(actor));
    }

    @GetMapping("/me/parties/logs")
    @Operation(summary = "나의 파티로그 조회",
            description = "내가 파티 로그를 작성한 적 있는 종료된 파티들을 최근에 끝난 순으로 조회")
    public RsData<PageDto<GetPartyResponse>> getLoggedPartiesByMe(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int pageSize
    ) {
        Member actor = this.userContext.getActor();

        return RsData.success(
                HttpStatus.OK,
                new PageDto<>(this.memberService.getPartiesLoggedByMe(actor, page, pageSize)));
    }

    @GetMapping("/{memberId}/parties")
    @Operation(summary = "다른 회원의 참여중인 파티 조회")
    public RsData<GetPartyMainResponse> getMembersParties(@PathVariable long memberId) {
        return RsData.success(HttpStatus.OK, this.memberService.getMembersParties(memberId));
    }

    @GetMapping("/{memberId}/parties/logs")
    @Operation(summary = "다른 회원의 파티로그 조회",
            description = "유저가 파티 로그를 작성한 적 있는 종료된 파티들을 최근에 끝난 순으로 조회")
    public RsData<PageDto<GetPartyResponse>> getLoggedPartiesByMembers(
            @PathVariable long memberId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int pageSize
    ) {
        return RsData.success(
                HttpStatus.OK,
                new PageDto<>(this.memberService.getPartiesLoggedByMember(memberId, page, pageSize)));
    }

    @GetMapping("/me/guilds")
    @Operation(summary = "내 가입 길드 조회")
    public RsData<List<GetGuildListResponse>> getMyGuilds() {
        Member actor = this.userContext.getActor();
        return RsData.success(HttpStatus.OK, guildMemberService.getMyGuilds(actor));
    }

    @GetMapping("/{memberId}/guilds")
    @Operation(summary = "특정 유저 가입 길드 조회")
    public RsData<List<GetGuildListResponse>> getMemberGuilds(@PathVariable Long memberId) {
        return RsData.success(HttpStatus.OK, guildMemberService.getMemberGuilds(memberId));
    }
}
