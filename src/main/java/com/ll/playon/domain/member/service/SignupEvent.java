package com.ll.playon.domain.member.service;

import com.ll.playon.domain.member.entity.Member;

public record SignupEvent(
        Member member
) {}
