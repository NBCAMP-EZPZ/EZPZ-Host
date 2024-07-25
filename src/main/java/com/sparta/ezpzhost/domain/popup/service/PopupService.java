package com.sparta.ezpzhost.domain.popup.service;

import com.sparta.ezpzhost.common.util.PageUtil;
import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.*;
import com.sparta.ezpzhost.domain.popup.entity.Image;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import com.sparta.ezpzhost.domain.popup.repository.popup.PopupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PopupService {

    private final PopupRepository popupRepository;
    private final ImageService imageService;

    /**
     * 팝업 등록
     *
     * @param requestDto 팝업 등록 정보
     * @param host 개최자
     * @return 팝업 정보
     */
    @Transactional
    public PopupResponseDto createPopup(PopupRequestDto requestDto, Host host) {

        // 팝업명 중복 체크
        duplicatedPopupName(requestDto.getName());

        // 썸네일 업로드
        ImageResponseDto thumbnail = imageService.uploadThumbnail(requestDto.getThumbnail());

        Popup popup = Popup.of(
                host, requestDto, thumbnail, PopupStatus.SCHEDULED, ApprovalStatus.REVIEWING);

        Popup savedPopup = popupRepository.save(popup);

        // 추가 사진 저장 및 업로드
        List<ImageResponseDto> images = imageService.saveImages(popup, requestDto.getImages());

        return PopupResponseDto.of(savedPopup, images);
    }

    /**
     * 상태별 팝업 목록 조회
     *
     * @param host     호스트
     * @param pageable 페이징
     * @param cond 조회 조건
     * @return 팝업 목록
     */
    public Page<?> findAllPopupsByStatus(Host host, Pageable pageable, PopupCondition cond) {
        return popupRepository.findAllPopupsByStatus(host, pageable, cond)
                .map(PopupPageResponseDto::of);
    }

    /**
     * 팝업 상세 조회
     * @param popupId 팝업 ID
     * @param host 호스트
     * @return 팝업 상세정보
     */
    public PopupResponseDto findPopup(Long popupId, Host host) {
        Popup popup = findPopupByIdAndHostId(popupId, host.getId());

        List<ImageResponseDto> images = imageService.findAllByPopup(popup);

        return PopupResponseDto.of(popup, images);
    }

    /**
     * 팝업 수정
     * @param popupId 팝업 ID
     * @param requestDto 팝업 수정 정보
     * @param host 호스트
     * @return 팝업 정보
     */
    @Transactional
    public PopupResponseDto updatePopup(Long popupId, PopupRequestDto requestDto, Host host) {

        // 팝업명 중복 체크
        duplicatedPopupName(requestDto.getName());

        // 팝업 권한 및 수정 가능 여부 확인
        Popup popup = findPopupByIdAndHostId(popupId, host.getId());
        popup.checkPossibleUpdateStatus();

        String thumbnailName = popup.getThumbnailName(); // 기존 썸네일
        List<Image> imageNames = imageService.findAllImageByPopup(popup); // 기존 추가 사진

        List<MultipartFile> updateImage = requestDto.getImages(); // 추가 업로드할 사진
        List<Image> deleteImage = imageNames.stream() // 삭제할 사진
                .filter(img -> {
                    for (int i = 0; i < updateImage.size(); i++) {
                        if (img.getName().equals(updateImage.get(i).getOriginalFilename())) {
                            log.info("Image : " + updateImage.get(i).getOriginalFilename());
                            updateImage.remove(i);
                            return false; // 사진명이 같다면 필터링
                        }
                    }
                    return true;
                })
                .toList();

        // 썸네일 변경 확인
        if (!thumbnailName.equals(requestDto.getThumbnail().getOriginalFilename())) {
            // 썸네일 업로드 및 이전 썸네일 삭제 (S3)
            ImageResponseDto updateThumbnail = imageService.uploadThumbnail(requestDto.getThumbnail());
            popup.updateThumbnail(updateThumbnail);
            imageService.deleteThumbnail(thumbnailName);
        }

        // 새로운 추가 사진 업로드 여부
        if (!updateImage.isEmpty()) {
            // 추가 사진 업로드
            List<ImageResponseDto> updateImages = imageService.saveImages(popup, updateImage);
        }

        // 이전 추가 사진 삭제 여부
        if (!deleteImage.isEmpty()) {
            // 이전 추가 사진 삭제 (S3)
            imageService.deleteImages(deleteImage);
        }

        // 팝업 정보 수정
        popup.update(requestDto);
        Popup savedPopup = popupRepository.save(popup);

        List<ImageResponseDto> images = imageService.findAllByPopup(savedPopup);

        return PopupResponseDto.of(savedPopup, images);
    }

    /**
     * 팝업 취소
     * @param popupId 팝업 ID
     * @param host 호스트
     */
    @Transactional
    public void cancelPopup(Long popupId, Host host) {
        Popup popup = findPopupByIdAndHostId(popupId, host.getId());
        popup.checkCancellationPossible();

        String thumbnailName = popup.getThumbnailName();
        List<Image> imageNames = imageService.findAllImageByPopup(popup);

        imageService.deleteImages(imageNames);
        imageService.deleteThumbnail(thumbnailName);

        popup.cancelPopup();
    }

    /**
     * 팝업 찾기
     * @param popupId 팝업 ID
     * @return 팝업
     */
    public Popup findPopupByIdAndHostId(Long popupId, Long hostId) {
        return popupRepository.findByIdAndHostId(popupId, hostId)
                .orElseThrow(()-> new CustomException(ErrorType.POPUP_ACCESS_FORBIDDEN));
    }

    /**
     * 팝업명 중복 확인
     * @param popupName 팝업명
     */
    private void duplicatedPopupName(String popupName) {
        if (popupRepository.existsByName(popupName)) {
            throw new CustomException(ErrorType.DUPLICATED_POPUP_NAME);
        }
    }
}
