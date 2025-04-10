package com.ll.playon.global.aws.s3;

import com.ll.playon.domain.image.type.ImageType;
import java.util.UUID;

public class S3Util {
    public static String buildS3Key(ImageType imageType, long referenceId, String fileType) {
        String key = "/" + UUID.randomUUID() + "." + fileType;

        return switch (imageType) {
            case GUILD -> "guild/" + referenceId + key;
            case LOG -> "log/" + referenceId + key;
            case BOARD -> "board/" + referenceId + key;
            case GUILDBOARD -> "guildboard/" + referenceId + key;
        };
    }

    public static String extractS3KeyFromUrl(String url) {
        int domainEndIdx = url.indexOf(".com/");

        return domainEndIdx != -1 ? url.substring(domainEndIdx + 5) : null;
    }

    public static String getFolderPath(ImageType imageType, long referenceId) {
        String type = switch (imageType) {
            case GUILD -> "guild/";
            case LOG -> "log/";
            case BOARD -> "board/";
            case GUILDBOARD -> "guildboard/";
        };

        return type + referenceId + "/";
    }
}
