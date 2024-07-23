package com.sparta.ezpzhost.domain.host.controller;

import com.sparta.ezpzhost.common.dto.CommonResponse;
import com.sparta.ezpzhost.common.security.UserDetailsImpl;
import com.sparta.ezpzhost.domain.host.dto.SignupRequestDto;
import com.sparta.ezpzhost.domain.host.dto.HostResponseDto;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.host.service.HostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HostController {

    private final HostService hostService;

    /**
     * 개최자 회원가입
     *
     * @param dto 회원가입 시 필요한 정보
     * @return 회원가입된 개최자 정보와 응답 메시지를 포함한 ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<?>> signup(
            @Valid @RequestBody SignupRequestDto dto) {

        Host host = hostService.signup(dto);
        return getResponseEntity(HostResponseDto.of(host), "회원가입 성공");
    }

    /**
     * 개최자 로그아웃
     *
     * @param userDetails 개최자 인증 정보
     * @return 응답 메시지만 포함한 ResponseEntity
     */
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<?>> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        hostService.logout(userDetails.getHost());
        return getResponseEntity("로그아웃 성공");
    }

}
