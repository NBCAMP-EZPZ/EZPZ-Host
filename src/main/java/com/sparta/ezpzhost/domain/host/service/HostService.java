package com.sparta.ezpzhost.domain.host.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.dto.SignupRequestDto;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.host.repository.RefreshTokenRepository;
import com.sparta.ezpzhost.domain.host.repository.HostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HostService {

    private final PasswordEncoder passwordEncoder;
    private final HostRepository hostRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 이용자 회원가입
     *
     * @param dto 회원가입 시 필요한 정보
     * @return 회원가입된 이용자 엔티티
     */
    @Transactional
    public Host signup(SignupRequestDto dto) {
        if (hostRepository.existsByUsername(dto.getUsername())) {
            throw new CustomException(ErrorType.DUPLICATED_USERNAME);
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        return hostRepository.save(Host.of(dto, encodedPassword));
    }

    /**
     * 이용자 로그아웃
     *
     * @param host 로그아웃 요청한 이용자
     */
    @Transactional
    public void logout(Host host) {
        refreshTokenRepository.deleteById(host.getUsername());
    }

}
