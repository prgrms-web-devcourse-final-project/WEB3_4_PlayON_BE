package com.ll.playon.domain.image.service;

import com.ll.playon.domain.image.entity.Image;
import com.ll.playon.domain.image.mapper.ImageMapper;
import com.ll.playon.domain.image.repository.ImageRepository;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.global.aws.s3.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    // 이미지 리스트 DB에 저장
    @Transactional
    public void saveImages(ImageType imageType, long referenceId, List<String> urls) {
        List<Image> images = urls.stream()
                .map(url -> ImageMapper.of(url, referenceId, imageType))
                .toList();

        this.imageRepository.saveAll(images);
    }

    // 이미지 DB에 저장
    @Transactional
    public void saveImage(ImageType imageType, long referenceId, String url) {
        this.imageRepository.save(ImageMapper.of(url, referenceId, imageType));
    }

    // Id로 이미지 목록 호출
    @Transactional(readOnly = true)
    public List<Image> getAllImagesById(ImageType imageType, long referenceId) {
        return this.imageRepository.findAllByImageTypeAndReferenceId(imageType, referenceId);
    }

    // Id로 이미지 이미지 호출
    @Transactional(readOnly = true)
    public String getImageById(ImageType imageType, long referenceId) {
        Image image = this.imageRepository.findByImageTypeAndReferenceId(imageType, referenceId);

        return image != null ? image.getImageUrl() : "";
    }

    // DB에서 해당 Id의 이미지 전부 삭제
    @Transactional
    public void deleteAllImagesById(ImageType imageType, long referenceId) {
        long imageDeleteCount = this.imageRepository.deleteByImageTypeAndReferenceId(imageType, referenceId);

        if (imageDeleteCount > 0) {
            this.s3Service.deleteAllObjectsById(ImageType.LOG, referenceId);
        }
    }

    // DB에서 해당 Id의 이미지 삭제
    @Transactional
    public void deleteImageById(ImageType imageType, long referenceId) {
        long imageDeleteCount = this.imageRepository.deleteByImageTypeAndReferenceId(imageType, referenceId);

        if (imageDeleteCount > 0) {
            this.s3Service.deleteObjectById(ImageType.LOG, referenceId);
        }
    }

    // DB에서 해당 ID에 존재하는 URL 모두 삭제
    @Transactional
    public void deleteImagesByIdAndUrls(ImageType imageType, long referenceId, List<String> urls) {
        long imageDeleteCount = !CollectionUtils.isEmpty(urls)
                ? this.imageRepository.deleteByReferenceIdAndImageUrl(imageType, referenceId, urls) : 0;

        if (imageDeleteCount > 0) {
            this.s3Service.deleteObjectsByUrl(urls);
        }
    }

    // DB에서 해당 ID에 존재하는 URL 삭제
    @Transactional
    public void deleteImagesByIdAndUrl(ImageType imageType, long referenceId, String url) {
        long imageDeleteCount = StringUtils.isNotBlank(url)
                ? this.imageRepository.deleteByReferenceIdAndImageUrl(imageType, referenceId, url) : 0;

        if (imageDeleteCount > 0) {
            this.s3Service.deleteObjectByUrl(url);
        }
    }
}
