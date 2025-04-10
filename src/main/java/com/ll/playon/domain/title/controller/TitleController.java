package com.ll.playon.domain.title.controller;

import com.ll.playon.domain.title.entity.TitleDto;
import com.ll.playon.domain.title.service.MemberTitleService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/titles")
@RequiredArgsConstructor
@Tag(name = "TitleController")
public class TitleController {

    private final UserContext userContext;
    private final MemberTitleService memberTitleService;


    @GetMapping()
    @Operation(summary = "사용자의 모든 칭호 조회")
    public RsData<List<TitleDto>> getTitles() {
        return RsData.success(HttpStatus.OK,memberTitleService.getMemberTitle(userContext.getActor()));
    }

    @PatchMapping("/{titleId}")
    @Operation(summary = "사용자의 대표 칭호 변경")
    public RsData<String> setRepresentativeTitle(@PathVariable Long titleId) {
        memberTitleService.setRepresentativeTitle(titleId, userContext.getActor());
        return RsData.success(HttpStatus.OK, "성공");
    }
}
