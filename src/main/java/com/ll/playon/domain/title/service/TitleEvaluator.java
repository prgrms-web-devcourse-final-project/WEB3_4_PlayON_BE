package com.ll.playon.domain.title.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.repository.MemberTitleService;
import com.ll.playon.domain.title.entity.MemberStat;
import com.ll.playon.domain.title.entity.Title;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.repository.MemberStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TitleEvaluator {

    private final TitleService titleService;
    private final MemberTitleService memberTitleService;
    private final MemberStatRepository memberStatRepository;

    public void check(ConditionType conditionType, Member member) {
        checkLogic(conditionType, member, 1);
    }
    public void gameCountCheck(ConditionType conditionType, Member member, int count) {
        checkLogic(conditionType, member, count);
    }
    public void checkLogic(ConditionType conditionType, Member member, int count) {
        // 해당 타입의 칭호 조회 (필요값으로 오름차순 정렬)
        final List<Title> titleList = titleService.findByConditionTypeOrderByConditionValueAsc(conditionType);

        // 사용자의 기록 조회, 1회 늘리기
        final MemberStat memberStat = memberStatRepository.findByMemberAndConditionType(member, conditionType)
                        .orElse(MemberStat.builder().member(member).conditionType(conditionType).build());
        memberStatRepository.save(memberStat.addStat(count));

        // 조건 검사 및 칭호 획득
        for (Title title : titleList) {
            if (memberStat.getStatValue() >= title.getConditionValue()) {
                memberTitleService.acquireTitle(member, title);
            }
        }
    }
}
