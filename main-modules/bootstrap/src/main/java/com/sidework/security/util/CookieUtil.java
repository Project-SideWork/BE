package com.sidework.security.util;

import com.sidework.security.exception.InvalidTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtil {
    private static final int COOKIE_EXPIRE_TIME = 30 * 60; // 30분

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRE_TIME);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);  // HTTPS 요청에만 secure 설정
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    public ResponseCookie createResponseCookie(String refreshToken){
        return ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict") // CSRF 방어
                .path("/")      // 모든 경로에서 유효
                .maxAge(COOKIE_EXPIRE_TIME) // 7일 유지
                .build();
    }


    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Optional.ofNullable(request.getCookies())
                .ifPresent(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> name.equals(cookie.getName()))
                        .forEach(cookie -> {
                            cookie.setValue("");  // 필요하지 않음, setMaxAge(0)으로 충분함
                            cookie.setPath("/");
                            cookie.setMaxAge(0);  // 쿠키 삭제
                            cookie.setHttpOnly(true);
                            cookie.setSecure(request.isSecure());
                            response.addCookie(cookie);
                        }));
    }

    public static Optional<String> getRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public static String findTokenOrThrow(HttpServletRequest request){
        return getRefreshTokenFromRequest(request)
                .orElseThrow(InvalidTokenException::new);
    }
}
