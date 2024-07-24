package com.sparta.ezpzhost.common.security;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.domain.host.entity.RefreshToken;
import com.sparta.ezpzhost.domain.host.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.sparta.ezpzhost.common.exception.ErrorType.INVALID_REFRESH_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    public static final String JWT_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token.ttl}")
    private int accessTokenTtl;

    @Value("${jwt.secret.key}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * Access 토큰 생성
     */
    public String createAccessToken(String username) {
        Date date = new Date();
        return JWT_PREFIX + Jwts.builder()
                .setSubject(username)
                .setIssuedAt(date) // 발급일
                .setExpiration(new Date(date.getTime() + accessTokenTtl))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Cookie에 Refresh 토큰 저장
     */
    public void saveRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
//                .secure(true)
                .path("/")
                .maxAge(accessTokenTtl)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Header에서 Access 토큰 가져오기
     */
    public String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JWT_PREFIX)) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }

    /**
     * Cookie에서 Refresh 토큰 가져오기
     */
    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN_COOKIE)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Access 토큰 검증
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 JWT 입니다.");
        }
        return false;
    }

    /**
     * Refresh 토큰 검증
     */
    public boolean validateRefreshToken(String refreshToken) {
        log.info("리프레시 토큰 검증");
        return refreshTokenRepository.findByRefreshToken(refreshToken).isPresent();
    }

    /**
     * Access 토큰에서 username 가져오기
     */
    public String getUsernameFromAccessToken(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(accessToken).getBody().getSubject();
    }

    /**
     * Refresh 토큰에서 username 가져오기
     */
    public String getUsernameFromRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(INVALID_REFRESH_TOKEN));
        return token.getUsername();
    }

}
