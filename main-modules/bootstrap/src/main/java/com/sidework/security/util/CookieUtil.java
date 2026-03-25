package com.sidework.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.oauth2.client.jackson2.OAuth2ClientJackson2Module;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Component
public class CookieUtil {
    public static final int COOKIE_EXPIRE_TIME = 30 * 60; // 30분
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new CoreJackson2Module())
            .registerModule(new OAuth2ClientJackson2Module());

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRE_TIME);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);  // HTTPS 요청에만 secure 설정
        cookie.setAttribute("SameSite", "Lax");
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

    public static String getAccessTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "access".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);  // Optional을 String으로
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

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    // TODO: setDomain
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setDomain("");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }


    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    cookie.setSecure(false);
                    cookie.setAttribute("SameSite", "None");
                    response.addCookie(cookie);
                }
            }
        }
    }
    public static String serialize(Object object) {
        try {
            return Base64.getUrlEncoder()
                    .encodeToString(objectMapper.writeValueAsBytes(object));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화 실패", e);
        }
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
            return objectMapper.readValue(bytes, cls);
        } catch (IOException e) {
            throw new RuntimeException("역직렬화 실패", e);
        }
    }
}
