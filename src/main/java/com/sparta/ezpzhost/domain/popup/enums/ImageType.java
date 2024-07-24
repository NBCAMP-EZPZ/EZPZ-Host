package com.sparta.ezpzhost.domain.popup.enums;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Getter
public enum ImageType {

    JPEG("image/jpeg"),
    PJPEG("image/pjpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    BMP("image/bmp"),
    X_WINDOWS_BMP("image/x-windows-bmp");

    private final String TYPE;
    private static final Long MAX_SIZE = 10L * 1024L * 1024L; // 최대 용량 제한 10MB

    ImageType(String type) {
        this.TYPE = type;
    }

    public static boolean isImageType(String fileType) {
        return Arrays.stream(ImageType.values()).anyMatch(imgFileType -> imgFileType.TYPE.equals(fileType));
    }

    public static void checkLimit(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new CustomException(ErrorType.IMAGE_LIMIT_EXCEEDED);
        }
    }
}
