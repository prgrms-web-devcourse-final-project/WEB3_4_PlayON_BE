package com.ll.playon.domain.party.partyLog.service;

import com.ll.playon.domain.image.service.ImageService;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.context.PartyMemberContext;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.repository.PartyMemberRepository;
import com.ll.playon.domain.party.party.validation.PartyMemberValidation;
import com.ll.playon.domain.party.partyLog.PartyLogRepository;
import com.ll.playon.domain.party.partyLog.dto.request.PostPartyLogRequest;
import com.ll.playon.domain.party.partyLog.dto.response.PostPartyLogResponse;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;
import com.ll.playon.domain.party.partyLog.mapper.PartyLogMapper;
import com.ll.playon.domain.party.partyLog.validation.PartyLogValidation;
import com.ll.playon.global.annotation.ActivePartyMemberOnly;
import com.ll.playon.global.aws.s3.S3Service;
import com.ll.playon.global.exceptions.ErrorCode;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyLogService {
    private final ImageService imageService;
    private final S3Service s3Service;
    private final PartyLogRepository partyLogRepository;
    private final PartyMemberRepository partyMemberRepository;

    // 파티 로그 작성
    // AOP로 권한 체크
    @ActivePartyMemberOnly
    @Transactional
    public PostPartyLogResponse createPartyLog(Member actor, long partyId, PostPartyLogRequest request) {
        Party party = PartyContext.getParty();
        PartyMember partyMember = PartyMemberContext.getPartyMember();

        PartyLogValidation.checkIsPartyLogNotCreated(partyMember);
        PartyLogValidation.checkIsPartyEnd(party);

        PartyLog partyLog = this.partyLogRepository.save(PartyLogMapper.of(partyMember, request));

        // Mvp를 투표한 경우
        if (request.partyMemberId() != null) {
            PartyMember mvpCandidate = this.partyMemberRepository.findById(request.partyMemberId())
                    .orElseThrow(ErrorCode.PARTY_MEMBER_NOT_FOUND::throwServiceException);

            mvpCandidate.voteMvp();
        }

        URL presignedUrl = this.s3Service.generatePresignedUrl(ImageType.LOG, partyId, request.fileType());

        return new PostPartyLogResponse(partyLog.getId(), partyLog.getId(), presignedUrl);
    }

    // 스크린샷 URL 저장
    // AOP로 권한 체크
    @ActivePartyMemberOnly
    public void saveImageUrl(Member actor, long partyId, long logId, String url) {
        Party party = PartyContext.getParty();

        PartyMemberValidation.checkIsPartyMemberOwn(PartyMemberContext.getPartyMember(), actor);
        PartyLogValidation.checkIsPartyEnd(party);

        this.imageService.saveImage(ImageType.LOG, logId, url);
    }
}
