package com.sparta.ezpzhost.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ezpzhost.common.security.dto.AuthenticatedResponse;
import com.sparta.ezpzhost.common.security.dto.UnauthenticatedResponse;
import com.sparta.ezpzhost.domain.host.dto.LoginRequestDto;
import com.sparta.ezpzhost.domain.host.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(TokenProvider tokenProvider, RefreshTokenService refreshTokenService) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
        setFilterProcessesUrl("/api/v1/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            // json to object
            LoginRequestDto dto = new ObjectMapper().readValue(req.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUsername(),
                            dto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authResult) throws IOException {
        log.info("로그인 성공 및 토큰 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();

        // 토큰 생성
        String accessToken = tokenProvider.createAccessToken(username);
        String refreshToken = UUID.randomUUID().toString();

        // 헤더에 액세스 토큰 추가
        res.addHeader(TokenProvider.ACCESS_TOKEN_HEADER, accessToken);

        // 쿠키에 리프레시 토큰 추가
        tokenProvider.saveRefreshTokenToCookie(refreshToken, res);

        // DB에 리프레시 토큰이 이미 있으면 변경, 없으면 저장
        refreshTokenService.save(username, refreshToken);

        // JSON 응답 생성
        res.setStatus(SC_OK);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        String json = new ObjectMapper().writeValueAsString(
                new AuthenticatedResponse(accessToken, "로그인 성공")
        );
        res.getWriter().write(json);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) throws IOException {
        log.error("로그인 실패 : {}", failed.getMessage());

        // JSON 응답 생성
        res.setStatus(SC_UNAUTHORIZED);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        String json = new ObjectMapper().writeValueAsString(
                new UnauthenticatedResponse("로그인 실패: " + failed.getMessage())
        );
        res.getWriter().write(json);
    }

}
