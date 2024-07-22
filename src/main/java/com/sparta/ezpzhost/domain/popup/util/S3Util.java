package com.sparta.ezpzhost.domain.popup.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.*;
import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.enums.ImageType;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    // AWS S3 클라이언트
    private final AmazonS3 amazonS3;
    // 파일 확장자 변조 체크
    private final Tika tika = new Tika();

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    /**
     * AWS S3 파일 업로드
     *
     * @param file    파일
     * @param dirName 폴더 이름
     * @return 파일 url
     */
    public ImageResponseDto uploadFile(MultipartFile file, String dirName) {
        if (file.isEmpty()) {
            return null;
        }

        validImageFile(file);


        String originName = file.getOriginalFilename(); // 원본 파일명
        String extension = StringUtils.getFilenameExtension(originName); // 확장자
        String saveName = generateSaveFilename(originName);
        String saveDir = dirName + "/" + saveName;

        ObjectMetadata metadata = new ObjectMetadata(); // 메타데이터
        metadata.setContentType(Mimetypes.getInstance().getMimetype(saveName));
        metadata.setContentLength(file.getSize());

        try {
            // AWS S3 파일 업로드
            PutObjectResult putObjectResult = amazonS3.putObject(
                    new PutObjectRequest(bucketName, saveDir, file.getInputStream(), metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new CustomException(ErrorType.UPLOAD_FAILED);
        }

        // 데이터베이스에 저장할 파일이 저장된 주소와 저장된 이름
        return new ImageResponseDto(saveDir, amazonS3.getUrl(bucketName, saveDir).toString());
    }

    /**
     * AWS S3 파일 삭제
     * @param fileName 파일 url
     */
    public void deleteFile(String fileName) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, fileName);
        amazonS3.deleteObject(request);
    }

    /**
     * 이미지 파일 확장자 및 용량 체크
     * @param file 파일
     */
    private void validImageFile(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String mimeType = tika.detect(inputStream);

            if (ImageType.isImageType(mimeType)) {
                ImageType.checkLimit(file);
            }else {
                throw new CustomException(ErrorType.UNSUPPORTED_MEDIA_TYPE);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 저장할 파일명 생성
     * @param filename 원본 파일명
     * @return 저장할 파일명
     */
    private String generateSaveFilename(final String filename) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = StringUtils.getFilenameExtension(filename);
        return uuid + "." + extension;
    }
}
