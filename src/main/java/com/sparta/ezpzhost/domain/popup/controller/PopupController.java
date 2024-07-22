package com.sparta.ezpzhost.domain.popup.controller;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.PopupRequestDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupResponseDto;
import com.sparta.ezpzhost.domain.popup.service.PopupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    /**
     * 팝업 등록
     * @param requestDto 팝업 등록 정보
     * @return 팝업 정보
     */
    @PostMapping("/v1/popups")
    public ResponseEntity<?> createPopup(
            @ModelAttribute @Valid PopupRequestDto requestDto) {
        // todo : securiry 구현 완료 시 변경
        Host host = new Host();
        PopupResponseDto responseDto = popupService.createPopup(requestDto, host);
        return getResponseEntity(responseDto, "팝업스토어 등록 성공");
    }
}
