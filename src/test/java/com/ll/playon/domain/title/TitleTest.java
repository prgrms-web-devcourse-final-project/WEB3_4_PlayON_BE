package com.ll.playon.domain.title;

import com.ll.playon.domain.member.TestMemberHelper;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.title.entity.MemberStat;
import com.ll.playon.domain.title.entity.MemberTitle;
import com.ll.playon.domain.title.entity.Title;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.repository.MemberStatRepository;
import com.ll.playon.domain.title.repository.MemberTitleRepository;
import com.ll.playon.domain.title.service.MemberTitleService;
import com.ll.playon.domain.title.repository.TitleRepository;
import com.ll.playon.domain.title.service.TitleEvaluator;
import com.ll.playon.domain.title.service.TitleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class TitleTest {

    @Autowired
    private TitleEvaluator titleEvaluator;

    @Autowired
    private MemberTitleService memberTitleService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private MemberTitleRepository memberTitleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberStatRepository memberStatRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestMemberHelper testMemberHelper;

    @Test
    @DisplayName("칭호 획득 테스트")
    void acquireTitleTest() {
        // 테스트 사용자 생성
        Member member = Member.builder()
                .username("titleTestMember").build();
        memberRepository.save(member);

        // 사용자 기록 세팅
        memberStatRepository.save(MemberStat.builder()
                .member(member).conditionType(ConditionType.BOARD_POST_COUNT).statValue(10).build());

        // 칭호 작업
        titleEvaluator.check(ConditionType.BOARD_POST_COUNT, member);

        // 확인
        assertEquals(titleService.findByConditionTypeOrderByConditionValueAsc(ConditionType.BOARD_POST_COUNT),
                memberTitleService.findMemberTitle(member));
    }

    @Test
    @DisplayName("대표칭호 설정 테스트")
    void setRepresentativeTitleTest() throws Exception {
        // 테스트 사용자 생성
        Member member = Member.builder().username("titleTestMember").role(Role.USER).build();
        memberRepository.save(member);

        assertEquals("", memberTitleService.getRepresentativeTitle(member));

        // 사용자 칭호 생성
        Title t1 = titleRepository.findById(1L).get();
        Title t2 = titleRepository.findById(2L).get();
        Title t3 = titleRepository.findById(3L).get();

        MemberTitle mt1 = MemberTitle.builder().member(member).title(t1).build();
        MemberTitle mt2 = MemberTitle.builder().member(member).title(t2).isRepresentative(true).build();
        MemberTitle mt3 = MemberTitle.builder().member(member).title(t3).build();
        memberTitleRepository.saveAll(List.of(mt1, mt2, mt3));
        assertEquals(t2.getName(), memberTitleService.getRepresentativeTitle(member));

        // 대표 칭호 변경
        MockHttpServletRequestBuilder request = patch("/api/titles/" + t3.getId());
        testMemberHelper.requestWithUserAuth(member.getUsername(), request);

        // 확인
        assertEquals(t3.getName(), memberTitleService.getRepresentativeTitle(member));
    }
}
