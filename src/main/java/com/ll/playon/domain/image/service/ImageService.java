package com.ll.playon.domain.image.service;

import com.ll.playon.domain.image.entity.Image;
import com.ll.playon.domain.image.mapper.ImageMapper;
import com.ll.playon.domain.image.repository.ImageRepository;
import com.ll.playon.domain.image.type.ImageType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;

    // 이미지 DB에 저장
    @Transactional
    public void saveImages(ImageType imageType, long referenceId, List<String> urls) {
        List<Image> images = urls.stream()
                .map(url -> ImageMapper.of(url, referenceId, imageType))
                .toList();

        this.imageRepository.saveAll(images);
    }

    // Id로 이미지 목록 호출
    @Transactional(readOnly = true)
    public List<Image> getAllImagesById(ImageType imageType, long referenceId) {
        return this.imageRepository.findAllByImageTypeAndReferenceId(imageType, referenceId);
    }

    // DB에서 해당 Id의 이미지 전부 삭제
    @Transactional
    public long deleteAllImagesById(ImageType imageType, long referenceId) {
        return this.imageRepository.deleteByImageTypeAndReferenceId(imageType, referenceId);
    }

    // DB에서 해당 ID에 존재하는 URL 모두 삭제
    @Transactional
    public long deleteImagesByIdAndUrls(ImageType imageType, long referenceId, List<String> urls) {
        return !CollectionUtils.isEmpty(urls)
                ? this.imageRepository.deleteByReferenceIdAndImageUrl(imageType, referenceId, urls) : 0;
    }
}
