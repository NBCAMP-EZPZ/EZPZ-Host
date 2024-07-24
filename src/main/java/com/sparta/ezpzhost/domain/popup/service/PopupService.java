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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        if (popupRepository.existsByName(requestDto.getName())) {
            throw new CustomException(ErrorType.DUPLICATED_POPUP_NAME);
        }

        // 썸네일 업로드
        ImageResponseDto thumbnail = imageService.uploadThumbnail(requestDto.getThumbnail());

        Popup popup = Popup.of(
                host, requestDto, thumbnail, PopupStatus.SCHEDULED, ApprovalStatus.REVIEWING);

        Popup savedPopup = popupRepository.save(popup);

        // 추가 사진 저장 및 업로드
        List<String> imageUrls = imageService.saveImages(popup, requestDto.getImages());

        return PopupResponseDto.of(savedPopup, imageUrls);
    }

    /**
     * 상태별 팝업 목록 조회
     *
     * @param host     호스트
     * @param pageUtil 페이징 기준 정보
     * @param cond
     * @return 팝업 목록
     */
    public Page<?> findAllPopupsByStatus(Host host, PageUtil pageUtil, PopupCondition cond) {
        return popupRepository.findAllPopupsByStatus(host, pageUtil, cond)
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

        List<String> imageUrls = imageService.findAllByPopup(popup);

        return PopupResponseDto.of(popup, imageUrls);
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
        if (popupRepository.existsByName(requestDto.getName())) {
            throw new CustomException(ErrorType.DUPLICATED_POPUP_NAME);
        }

        Popup popup = findPopupByIdAndHostId(popupId, host.getId());

        String thumbnailName = popup.getThumbnailName();
        List<Image> imageNames = imageService.findAllImageByPopup(popup);

        // 썸네일 업로드
        ImageResponseDto updateThumbnail = imageService.uploadThumbnail(requestDto.getThumbnail());
        // 추가 사진 저장 및 업로드
        List<String> updateImageUrls = imageService.saveImages(popup, requestDto.getImages());

        popup.updateThumbnail(updateThumbnail);
        popup.update(requestDto);
        Popup savedPopup = popupRepository.save(popup);

        // 이전 썸네일, 추가 사진 삭제 (S3)
        imageService.deleteThumbnail(thumbnailName);
        imageService.deleteImages(imageNames);

        return PopupResponseDto.of(savedPopup, updateImageUrls);
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
}
