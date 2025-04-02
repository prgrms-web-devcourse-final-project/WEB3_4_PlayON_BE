package com.ll.playon.domain.member;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.service.MemberService;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Component
public class TestMemberHelper {

    private final MemberService memberService;
    private final MockMvc mvc;

    public TestMemberHelper(MemberService memberService, MockMvc mvc) {
        this.memberService = memberService;
        this.mvc = mvc;
    }

    public ResultActions requestWithUserAuth(String username, MockHttpServletRequestBuilder request) throws Exception {
        Member actor = memberService.findByUsername(username).get();
        String actorAuthToken = memberService.genAccessToken(actor);

        return mvc.perform(request
                .header("Authorization", "Bearer " + actor.getApiKey() + " " + actorAuthToken)
        ).andDo(print());
    }
}