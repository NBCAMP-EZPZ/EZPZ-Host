package com.sparta.ezpzhost.domain.popup.service;

import com.sparta.ezpzhost.common.dto.PageDto;
import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupPageResponseDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupRequestDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupResponseDto;
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
     * @param host 호스트
     * @param pageDto 페이징 기준 정보
     * @return 팝업 목록
     */
    public Page<?> findAllPopupsByStatus(Host host, PageDto pageDto) {
        // todo : Host 구현 완료 시 수정
        String companyName = "companyName";

        return popupRepository.findAllPopupsByStatus(host, pageDto)
                .map(p -> PopupPageResponseDto.of(p.getId(), p.getName(), companyName));
    }

    /**
     * 팝업 상세 조회
     * @param popupId 팝업 ID
     * @param host 호스트
     * @return 팝업 상세정보
     */
    public PopupResponseDto findPopup(Long popupId, Host host) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(()-> new CustomException(ErrorType.POPUP_ACCESS_FORBIDDEN));
        popup.verifyHostOfPopup(host);

        List<String> imageUrls = imageService.findAllByPopup(popup);

        return PopupResponseDto.of(popup, imageUrls);
    }
}
