package com.ll.playon.global.aws.s3;

import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    // PresignedURL 여러개 생성
    public List<URL> generatePresignedUrls(ImageType imageType, long referenceId, List<String> fileTypes) {
        if (CollectionUtils.isEmpty(fileTypes)) {
            return List.of();
        }

        try {
            return fileTypes.stream()
                    .map(fileType -> this.generatePresignedUrl(imageType, referenceId, fileType))
                    .toList();
        } catch (SdkException e) {
            throw ErrorCode.S3_PRESIGNED_URL_GENERATION_FAILED.throwServiceException();
        }
    }

    // PresignedURL 생성
    public URL generatePresignedUrl(ImageType imageType, long referenceId, String fileType) {
        try {
            // PresignedURL 생성
            return s3Presigner.presignPutObject(builder -> builder
                            .putObjectRequest(this.buildPutObjectRequest(imageType, referenceId, fileType))
                            .signatureDuration(Duration.ofMinutes(3)))
                    .url();
        } catch (SdkException e) {
            throw ErrorCode.S3_PRESIGNED_URL_GENERATION_FAILED.throwServiceException();
        }
    }

    // List의 S3 객체 모두 삭제
    public void deleteObjectsByUrl(List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            return;
        }

        try {
            // 삭제할 키가 포함되는 ObjectIdentifier 생성
            List<ObjectIdentifier> objectIdentifiers = urls.stream()
                    .map(S3Util::extractS3KeyFromUrl)
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .toList();

            // 키가 포함되면 S3에서 삭제
            s3Client.deleteObjects(this.buildDeleteObjectsRequest(objectIdentifiers));
        } catch (SdkException e) {
            throw ErrorCode.S3_OBJECT_DELETE_FAILED.throwServiceException();
        }
    }

    // URL을 포함한 S3 객체 삭제
    public void deleteObjectByUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }

        try {
            // 삭제할 키가 포함되는 ObjectIdentifier 생성
            String key = S3Util.extractS3KeyFromUrl(url);
            
            if (key == null) {
                return;
            }
            
            // 키가 포함되면 S3에서 삭제
            s3Client.deleteObject(this.buildDeleteObjectRequest(key));
        } catch (Exception e) {
            throw ErrorCode.S3_OBJECT_DELETE_FAILED.throwServiceException();
        }
    }

    // S3 폴더 내의 모든 객체 삭제
    public void deleteAllObjectsById(ImageType imageType, long referenceId) {
        String folderPath = S3Util.getFolderPath(imageType, referenceId);

        try {
            // 삭제할 폴더 내 모든 객체 조회
            List<S3Object> objects = this.getAllS3ObjectsInFolderPath(folderPath);

            if (objects.isEmpty()) {
                return;
            }

            // 삭제할 키 목록 생성
            List<ObjectIdentifier> objectIdentifiers = objects.stream()
                    .map(object -> ObjectIdentifier.builder().key(object.key()).build())
                    .toList();

            s3Client.deleteObjects(this.buildDeleteObjectsRequest(objectIdentifiers));
        } catch (SdkException e) {
            throw ErrorCode.S3_OBJECT_DELETE_FAILED.throwServiceException();
        }
    }

    // S3 폴더 내의 단일 객체 삭제
    public void deleteObjectById(ImageType imageType, long referenceId) {
        String folderPath = S3Util.getFolderPath(imageType, referenceId);

        try {
            // 삭제할 폴더 내 모든 객체 조회
            List<S3Object> objects = this.getAllS3ObjectsInFolderPath(folderPath);

            if (objects.isEmpty()) {
                return;
            }

            // 단일 객체 삭제
            String key = objects.getFirst().key();

            s3Client.deleteObject(this.buildDeleteObjectRequest(key));
        } catch (SdkException e) {
            throw ErrorCode.S3_OBJECT_DELETE_FAILED.throwServiceException();
        }
    }

    // S3 PutObjectRequest 생성
    private PutObjectRequest buildPutObjectRequest(ImageType imageType, long referenceId, String fileType) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(S3Util.buildS3Key(imageType, referenceId, fileType))
                .build();
    }

    // S3 DeleteObjectsRequest 생성
    private DeleteObjectsRequest buildDeleteObjectsRequest(List<ObjectIdentifier> objectIdentifiers) {
        return DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete -> delete.objects(objectIdentifiers))
                .build();
    }

    // S3 DeleteObjectsRequest 생성
    private DeleteObjectsRequest buildDeleteObjectsRequest(ObjectIdentifier objectIdentifier) {
        return DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete -> delete.objects(objectIdentifier))
                .build();
    }

    // S3 DeleteObjectRequest 생성
    private DeleteObjectRequest buildDeleteObjectRequest(String key) {
        return DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }

    // S3 폴더의 모든 객체 조회
    private List<S3Object> getAllS3ObjectsInFolderPath(String folderPath) {
        List<S3Object> objects = new ArrayList<>();

        try {
            // 다음 페이지 여부
            String continuationToken = null;
            do {
                ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(folderPath)
                        .continuationToken(continuationToken)
                        .build();

                ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
                objects.addAll(listResponse.contents());

                continuationToken = listResponse.nextContinuationToken();
            } while (continuationToken != null);    // 다음 페이지가 없을 때까지 반복
        } catch (SdkException e) {
            ErrorCode.S3_OBJECT_GET_FAILED.throwServiceException();
        }

        return objects;
    }
}
