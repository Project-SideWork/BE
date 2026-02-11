package com.sidework.security.handler;

import com.sidework.security.service.TokenBlackListService;
import com.sidework.security.util.CookieUtil;
import com.sidework.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;


import static com.sidework.security.util.CookieUtil.expireCookie;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenBlackListService tokenBlackListService;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 1) 쿠키에서 Access/Refresh 추출
        String refresh = CookieUtil.getRefreshTokenFromRequest(request);

        // 2) Refresh 폐기 (서버 저장소에서 삭제)
        if (refresh != null) {
            tokenBlackListService.addRefreshTokenBlackList(refresh, jwtUtil.getRefreshTokenExpireTime(refresh));
            expireCookie(response, "refresh", "/", true, true, "Strict");
            expireCookie(response, "access", "/", true, true, "Strict");
        }

        // 3) 보안 컨텍스트 정리
        SecurityContextHolder.clearContext();
    }
}
