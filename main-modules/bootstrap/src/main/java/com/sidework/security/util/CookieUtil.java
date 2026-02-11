package com.sidework.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtil {
    private static final int COOKIE_EXPIRE_TIME = 30 * 60; // 30분

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRE_TIME);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);  // HTTPS 요청에만 secure 설정
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    public static void expireCookie(
            HttpServletResponse res, String name, String path,
            boolean httpOnly, boolean secure, String sameSite
    ) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setMaxAge(0);

        cookie.setAttribute("SameSite", sameSite);
        res.addCookie(cookie);
    }

    public static String getRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);  // Optional을 String으로
    }
}
