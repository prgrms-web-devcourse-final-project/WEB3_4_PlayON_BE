package com.ll.playon.domain.party.partyLog.service;

import com.ll.playon.domain.image.service.ImageService;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.context.PartyContext;
import com.ll.playon.domain.party.party.context.PartyMemberContext;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.repository.PartyMemberRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.validation.PartyMemberValidation;
import com.ll.playon.domain.party.partyLog.dto.request.PostPartyLogRequest;
import com.ll.playon.domain.party.partyLog.dto.request.PutPartyLogRequest;
import com.ll.playon.domain.party.partyLog.dto.response.GetAllPartyLogResponse;
import com.ll.playon.domain.party.partyLog.dto.response.GetPartyLogResponse;
import com.ll.playon.domain.party.partyLog.dto.response.PartyLogResponse;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;
import com.ll.playon.domain.party.partyLog.event.ImageDeleteEvent;
import com.ll.playon.domain.party.partyLog.mapper.PartyLogMapper;
import com.ll.playon.domain.party.partyLog.repository.PartyLogRepository;
import com.ll.playon.domain.party.partyLog.validation.PartyLogValidation;
import com.ll.playon.global.annotation.ActivePartyMemberOnly;
import com.ll.playon.global.aws.s3.S3Service;
import com.ll.playon.global.exceptions.ErrorCode;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyLogService {
    private final ImageService imageService;
    private final S3Service s3Service;
    private final PartyRepository partyRepository;
    private final PartyLogRepository partyLogRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 파티 로그 작성
    // AOP로 권한 체크
    @ActivePartyMemberOnly
    @Transactional
    public PartyLogResponse createPartyLog(Member actor, long partyId, PostPartyLogRequest request) {
        Party party = PartyContext.getParty();
        PartyMember partyMember = PartyMemberContext.getPartyMember();

        PartyLogValidation.checkIsPartyLogNotCreated(partyMember);
        PartyLogValidation.checkIsPartyEnd(party);

        PartyLog partyLog = this.partyLogRepository.save(PartyLogMapper.of(partyMember, request));

        // Mvp를 투표한 경우
        if (request.partyMemberId() != null) {
            PartyMember mvpCandidate = this.partyMemberRepository.findById(request.partyMemberId())
                    .orElseThrow(ErrorCode.PARTY_MEMBER_NOT_FOUND::throwServiceException);

            PartyMemberValidation.checkPartyMember(party, partyMember);

            mvpCandidate.voteMvp();
        }

        return new PartyLogResponse(
                partyLog.getId(),
                partyId,
                this.savePartyLogImage(partyLog.getId(), request.fileType()));
    }

    // 로그 이미지 저장
    private URL savePartyLogImage(long logId, String fileType) {
        return fileType != null ? this.s3Service.generatePresignedUrl(ImageType.LOG, logId, fileType) : null;
    }

    // 스크린샷 URL 저장
    // AOP로 권한 체크
    @ActivePartyMemberOnly
    @Transactional
    public void saveImageUrl(Member actor, long partyId, long logId, String url) {
        if (url == null) {
            return;
        }

        Party party = PartyContext.getParty();

        PartyMemberValidation.checkIsNotPartyMemberOwn(PartyMemberContext.getPartyMember(), actor);
        PartyLogValidation.checkIsPartyEnd(party);

        this.imageService.saveImage(ImageType.LOG, logId, url);
    }

    // 파티의 모든 파티 로그 조회
    public GetAllPartyLogResponse getAllPartyLogs(long partyId) {
        Party party = this.partyRepository.findById(partyId)
                .orElseThrow(ErrorCode.PARTY_NOT_FOUND::throwServiceException);

        List<PartyMember> partyMembers = party.getPartyMembers();

        return new GetAllPartyLogResponse(partyMembers.stream()
                .map(PartyMember::getPartyLog)
                .filter(Objects::nonNull)
                .map(partyLog -> new GetPartyLogResponse(partyLog,
                        this.imageService.getImageById(ImageType.LOG, partyLog.getId()))
                )
                .toList());
    }

    // 파티 로그 수정
    // AOP로 권한 체크
    @ActivePartyMemberOnly
    @Transactional
    public PartyLogResponse updatePartyLog(Member actor, long partyId, long logId, PutPartyLogRequest request) {
        PartyMember partyMember = PartyMemberContext.getPartyMember();

        PartyMemberValidation.checkIsNotPartyMemberOwn(partyMember, actor);

        PartyLog partyLog = this.getPartyLog(logId);
        partyLog.update(request);

        this.imageService.deleteImagesByIdAndUrl(ImageType.LOG, logId, request.deleteUrl());

        return new PartyLogResponse(
                logId,
                partyId,
                this.savePartyLogImage(logId, request.newFileType()));
    }

    // 파티 로그 삭제
    // AOP로 권한 체크
    @ActivePartyMemberOnly
    @Transactional
    public void deletePartyLog(Member actor, long partyId, long logId) {
        PartyMember partyMember = PartyMemberContext.getPartyMember();

        PartyMemberValidation.checkIsNotPartyMemberOwn(partyMember, actor);

        PartyLog partyLog = this.getPartyLog(logId);

        partyLog.delete();

        eventPublisher.publishEvent(new ImageDeleteEvent(logId));
    }

    // logId로 PartyLog 조회
    private PartyLog getPartyLog(long logId) {
        return this.partyLogRepository.findById(logId)
                .orElseThrow(ErrorCode.PARTY_LOG_NOT_FOUND::throwServiceException);
    }
}
