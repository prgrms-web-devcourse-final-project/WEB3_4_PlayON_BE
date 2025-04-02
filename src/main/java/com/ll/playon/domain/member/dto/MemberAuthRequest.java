package com.ll.playon.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberAuthRequest(
    @NotBlank(message = "아이디를 입력하세요.") String username,
    @NotBlank(message = "비밀번호를 입력하세요.") String password
) {}
