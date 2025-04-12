package com.ll.playon.domain.board.service;

import com.ll.playon.domain.board.dto.MemberProfileDto;
import com.ll.playon.domain.board.dto.request.PostBoardRequest;
import com.ll.playon.domain.board.dto.request.PutBoardRequest;
import com.ll.playon.domain.board.dto.response.GetBoardDetailResponse;
import com.ll.playon.domain.board.dto.response.GetBoardListResponse;
import com.ll.playon.domain.board.dto.response.PostBoardResponse;
import com.ll.playon.domain.board.dto.response.PutBoardResponse;
import com.ll.playon.domain.board.entity.Board;
import com.ll.playon.domain.board.enums.BoardCategory;
import com.ll.playon.domain.board.enums.BoardSortType;
import com.ll.playon.domain.board.repository.BoardLikeRepository;
import com.ll.playon.domain.board.repository.BoardRepository;
import com.ll.playon.domain.guild.guild.dto.request.PostImageUrlRequest;
import com.ll.playon.domain.image.event.ImageDeleteEvent;
import com.ll.playon.domain.image.service.ImageService;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.global.aws.s3.S3Service;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.validation.FileValidator;
import com.ll.playon.global.validation.GlobalValidation;
import com.ll.playon.standard.page.dto.PageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final S3Service s3Service;
    private final ImageService imageService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MemberRepository memberRepository;
    private final BoardLikeRepository boardLikeRepository;

    @Transactional
    public PostBoardResponse createBoard(PostBoardRequest request, Member actor) {
        // 파일 형식 확인
        FileValidator.validateFileType(request.fileType());

        // 게시글 저장
        Board board = boardRepository.save(
                Board.builder()
                .author(actor)
                .title(request.title())
                .content(request.content())
                .category(request.category())
                .build()
        );
        return PostBoardResponse.builder()
                .boardId(board.getId())
                .presignedUrl(genGuildPresignedUrl(board.getId(), request.fileType()))
                .build();
    }

    // PresignedUrl 발급
    private URL genGuildPresignedUrl(Long boardId, String fileType) {
        if(ObjectUtils.isNotEmpty(fileType)) {
            return s3Service.generatePresignedUrl(ImageType.BOARD, boardId, fileType);
        }
        return null;
    }

    @Transactional
    public void saveImageUrl(long boardId, PostImageUrlRequest request) {
        // URL 확인
        if (ObjectUtils.isEmpty(request.url())) {
            throw ErrorCode.URL_NOT_FOUND.throwServiceException();
        }

        // 이미지 테이블 저장
        imageService.saveImage(ImageType.BOARD, boardId, request.url());
    }

    @Transactional
    public PutBoardResponse modifyBoard(long boardId, @Valid PutBoardRequest request, Member actor) {
        Board board = findBoardOrElseThrow(boardId);

        // 작성자 본인만 수정 가능
        validateBoardAuthor(board, actor);

        // 이미지 수정
        if (!request.newFileType().isBlank()) {
            // 파일 형식 확인
            FileValidator.validateFileType(request.newFileType());
            // 이미지 삭제
            imageService.deleteImagesByIdAndUrl(ImageType.BOARD, boardId, request.deleteUrl().toString());
        }

        board.update(request.title(), request.content(), request.category());

        return PutBoardResponse.builder()
                .boardId(board.getId())
                .presignedUrl(genGuildPresignedUrl(board.getId(), request.newFileType()))
                .build();
    }

    @Transactional
    public void deleteBoard(long boardId, Member actor) {
        Board board = findBoardOrElseThrow(boardId);

        // 작성자 본인만 삭제 가능
        validateBoardAuthor(board, actor);

        boardRepository.delete(board);

        applicationEventPublisher.publishEvent(new ImageDeleteEvent(board.getId(), ImageType.BOARD));
    }

    @Transactional(readOnly = true)
    public GetBoardDetailResponse getBoardDetail(long boardId, Member actor) {
        Board board = findBoardOrElseThrow(boardId);

        // 조회수 증가
        board.increaseHit();

        MemberProfileDto authorProfile = memberRepository.getProfile(board.getAuthor().getId())
                .orElse(MemberProfileDto.builder() // 방어차원..
                        .memberId(board.getAuthor().getId())
                        .nickname(board.getAuthor().getNickname())
                        .profileImg("")
                        .title("")
                        .build()
                );

        return GetBoardDetailResponse.builder()
                .boardId(board.getId())
                .authorNickname(authorProfile.nickname())
                .profileImg(authorProfile.profileImg()) // 프로필 이미지
                .title(authorProfile.title()) // 대표 칭호
                .isAuthor(isAuthor(board.getAuthor(), actor)) // 작성자 본인 여부
                .isLiked(isLiked(boardId, actor))
                .boardTitle(board.getTitle())
                .createAt(board.getCreatedAt())
                .imgUrl(imageService.getImageById(ImageType.BOARD, board.getId())) // 게시글 이미지
                .content(board.getContent())
                .hit(board.getHit())
                .like(board.getLikeCount())
                .boardCategory(board.getCategory().getValue())
                .build();
    }

    @Transactional(readOnly = true)
    public PageDto<GetBoardListResponse> getBoardList(int page, int pageSize, BoardSortType sort, String keyword, BoardCategory category) {
        GlobalValidation.checkPageSize(pageSize);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<GetBoardListResponse> result = boardRepository.findBoardList(category, keyword, sort.name(), pageable);

        return new PageDto<>(result);
    }

    private Board findBoardOrElseThrow(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwServiceException);
    }

    private void validateBoardAuthor(Board board, Member actor) {
        if (!board.getAuthor().getId().equals(actor.getId())) {
            throw ErrorCode.NO_BOARD_PERMISSION.throwServiceException();
        }
    }

    private boolean isAuthor(Member author, Member actor) {
        return actor != null && actor.getId().equals(author.getId());
    }

    private boolean isLiked(long boardId, Member actor) {
        return actor != null && boardLikeRepository.existsByBoardIdAndMemberId(boardId, actor.getId());
    }
}
