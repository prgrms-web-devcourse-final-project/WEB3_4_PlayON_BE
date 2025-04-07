package com.ll.playon.domain.title.repository;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.entity.MemberTitle;
import com.ll.playon.domain.title.entity.Title;
import com.ll.playon.domain.title.entity.TitleDto;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberTitleService {

    private final  MemberTitleRepository memberTitleRepository;

    public void acquireTitle(Member member, Title title) {
        if(memberTitleRepository.findByMemberAndTitle(member, title).isPresent()) return;

        memberTitleRepository.save(MemberTitle.builder()
                .member(member).title(title).build());
    }

    public List<TitleDto> getMemberTitle(Member actor) {
        List<TitleDto> response = new ArrayList<>();

        List<Title> titleList = findMemberTitle(actor);
        if(titleList.isEmpty()) return new ArrayList<>();

        for (Title title : titleList) {
            MemberTitle memberTitle = memberTitleRepository.findByMemberAndTitle(actor, title)
                    .orElseThrow(ErrorCode.TITLE_NOT_FOUND::throwServiceException);

            TitleDto titledto = TitleDto.builder()
                    .titleId(title.getId())
                    .name(title.getName())
                    .description(title.getDescription())
                    .acquiredAt(memberTitle.getAcquiredAt())
                    .isRepresentative(memberTitle.isRepresentative())
                    .build();
            response.add(titledto);
        }
        return response;
    }

    public List<Title> findMemberTitle(Member member) {
        return memberTitleRepository.findAllByMember(member).stream()
                .map(MemberTitle::getTitle).toList();
    }

    public void setRepresentativeTitle(Long titleId, Member actor) {
        MemberTitle memberTitle = memberTitleRepository.findByIdAndMember(titleId, actor)
                .orElseThrow(ErrorCode.AUTHORIZATION_FAILED::throwServiceException);

        memberTitleRepository.findAllByMember(memberTitle.getMember())
                        .forEach(mt -> mt.setRepresentative(false));

        memberTitle.setRepresentative(true);
    }

    public String getRepresentativeTitle(Member member) {
        Optional<MemberTitle> memberTitleOptional = memberTitleRepository.findByMemberAndIsRepresentativeTrue(member);
        if(memberTitleOptional.isPresent()) return memberTitleOptional.get().getTitle().getName();
        return ""; // 대표 칭호 설정하지 않았을 시 빈 String 반환
    }
}
