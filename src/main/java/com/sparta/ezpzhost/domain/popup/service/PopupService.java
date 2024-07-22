package com.sparta.ezpzhost.domain.popup.service;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.PopupRequestDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupResponseDto;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import com.sparta.ezpzhost.domain.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
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
     * @param host
     * @return 팝업 정보
     */
    @Transactional
    public PopupResponseDto createPopup(PopupRequestDto requestDto, Host host) {

        // 썸네일 업로드
        String thumbnailUrl = imageService.uploadThumbnail(requestDto.getThumbnail());

        Popup popup = Popup.of(
                host, requestDto, thumbnailUrl, PopupStatus.EXPECTED, ApprovalStatus.REVIEWING);

        Popup savedPopup = popupRepository.save(popup);

        // 추가 사진 저장 및 업로드
        List<String> imageUrl = imageService.saveImages(popup, requestDto.getImages());

        PopupResponseDto responseDto = PopupResponseDto.of(savedPopup);
        responseDto.addImages(imageUrl);

        return responseDto;
    }
}
