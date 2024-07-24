package com.sparta.ezpzhost.domain.popup.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.entity.Image;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.repository.ImageRepository;
import com.sparta.ezpzhost.common.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Util s3Util;

    /**
     * 추가 사진 DB 저장 및 S3 업로드
     * @param popup 팝업 정보
     * @param images 추가 사진 목록
     * @return 추가 사진 url
     */
    public List<String> saveImages(Popup popup, List<MultipartFile> images) {

        if (images.size() > 3 || images.isEmpty()) {
            throw new CustomException(ErrorType.IMAGE_COUNT_EXCEEDED);
        }

        List<Image> imageList = images.stream()
                .map(img -> Image.of(popup, uploadImage(img)))
                .toList();

        imageRepository.saveAll(imageList);

        return imageList.stream().map(Image::getUrl).toList();
    }

    /**
     * 단일 사진 S3 업로드
     * @param image 사진 리소스
     * @return 사진 정보
     */
    public ImageResponseDto uploadImage(MultipartFile image) {
        return s3Util.uploadFile(image, "image");
    }

    /**
     * 썸네일 S3 업로드
     * @param image 썸네일 리소스
     * @return 썸네일 url
     */
    public ImageResponseDto uploadThumbnail(MultipartFile image) {
        return s3Util.uploadFile(image, "thumbnail");
    }

    /**
     * 상품 사진 S3 업로드
     * @param image 상품 사진 리소스
     * @return 상품 사진 url
     */
    public ImageResponseDto uploadItemImage(MultipartFile image) {
        return s3Util.uploadFile(image, "item");
    }

    /**
     * 팝업으로 이미지 url 목록 조회
     * @param popup 팝업
     * @return 이미지 url 목록
     */
    public List<String> findAllByPopup(Popup popup) {
        return imageRepository.findAllByPopup(popup).stream()
                .map(Image::getUrl)
                .toList();
    }

    /**
     * 팝업으로 이미지 목록 조회
     * @param popup 팝업
     * @return 이미지 목록
     */
    public List<Image> findAllImageByPopup(Popup popup) {
        return imageRepository.findAllByPopup(popup);
    }

    /**
     * 썸네일 삭제 (S3)
     * @param thumbnailName 썸네일 저장명
     */
    public void deleteThumbnail(String thumbnailName) {
        s3Util.deleteFile(thumbnailName);
    }

    /**
     * 추가 사진 목록 삭제 (S3)
     * @param images 추가 사진 목록
     */
    public void deleteImages(List<Image> images) {
        if (CollectionUtils.isEmpty(images)) {
            return;
        }

        for (Image image : images) {
            s3Util.deleteFile(image.getName());
        }

        imageRepository.deleteAll(images);
    }

    /**
     * 상품 사진 삭제 (S3)
     * @param imageName 상품 사진 저장명
     */
    public void deleteItemImage(String imageName) {
        s3Util.deleteFile(imageName);
    }
}
