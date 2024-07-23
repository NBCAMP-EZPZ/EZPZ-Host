package com.sparta.ezpzhost.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ezpzhost.common.security.dto.AuthenticatedResponse;
import com.sparta.ezpzhost.common.security.dto.UnauthenticatedResponse;
import com.sparta.ezpzhost.domain.host.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = tokenProvider.getAccessToken(req);
        String refreshToken = tokenProvider.getRefreshToken(req);

        if (StringUtils.hasText(accessToken)) {
            // 액세스 토큰 검증
            if (tokenProvider.validateAccessToken(accessToken)) {
                log.info("액세스 토큰 검증 성공");
                setAuthentication(tokenProvider.getUsernameFromAccessToken(accessToken));
            } else if (StringUtils.hasText(refreshToken)) {
                log.info("액세스 토큰 만료");
                // 리프레시 토큰 검증
                if (tokenProvider.validateRefreshToken(refreshToken)) {
                    log.info("리프레시 토큰 검증 성공");
                    String username = tokenProvider.getUsernameFromRefreshToken(refreshToken); // error

                    log.info("새로운 액세스 토큰, 리프레시 토큰 발급 (RTR)");
                    String newAccessToken = tokenProvider.createAccessToken(username);
                    String newRefreshToken = UUID.randomUUID().toString();

                    log.info("리프레시 토큰 쿠키에 저장");
                    tokenProvider.saveRefreshTokenToCookie(newRefreshToken, res);

                    log.info("액세스 토큰 응답");
                    res.addHeader(TokenProvider.ACCESS_TOKEN_HEADER, newAccessToken);
                    setAuthentication(username);
                } else {
                    log.error("리프레시 토큰 검증 실패");
                    // 리프레시 토큰이 공격당한 것으로 간주, 리프레시 토큰 삭제
                    refreshTokenService.delete(refreshToken);
                    unverifiedRefreshTokenHandler(res);
                    return;
                }
            }
        }
        filterChain.doFilter(req, res);
    }

    /**
     * 인증 처리
     */
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(createAuthentication(username));
        SecurityContextHolder.setContext(context);
    }

    /**
     * 인증 객체 생성
     */
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * Refresh 토큰 검증 성공 핸들러
     */
    public void verifiedRefreshTokenHandler(HttpServletResponse res, String newAccessToken) throws IOException {
        res.setStatus(SC_OK);
        res.setContentType("application/json");
        String json = new ObjectMapper().writeValueAsString(
                new AuthenticatedResponse(newAccessToken, "리프레시 토큰 검증 성공 & 새로운 토큰 발급")
        );
        res.getWriter().write(json);
    }

    /**
     * Refresh 토큰 검증 실패 핸들러
     */
    public void unverifiedRefreshTokenHandler(HttpServletResponse res) throws IOException {
        res.setStatus(SC_UNAUTHORIZED);
        res.setContentType("application/json");
        String json = new ObjectMapper().writeValueAsString(
                new UnauthenticatedResponse("리프레시 토큰 검증 실패")
        );
        res.getWriter().write(json);
    }

}
