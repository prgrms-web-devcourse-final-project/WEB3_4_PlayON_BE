package com.ll.playon.domain.member.dto;

public record MemberAuthRequest(
    String username,
    String password
) {}
