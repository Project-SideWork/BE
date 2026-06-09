package com.sidework.security.filter;

import com.sidework.security.service.TokenBlackListService;
import com.sidework.security.util.CookieUtil;
import com.sidework.security.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlackListService tokenBlackListService;


    private record PermitRequest(HttpMethod method, String pattern) {
        boolean matches(HttpServletRequest request, AntPathMatcher pathMatcher) {
            return method.matches(request.getMethod())
                    && pathMatcher.match(pattern, request.getRequestURI());
        }
    }

    private record AnyMethodPermitRequest(String pattern) {
        boolean matches(HttpServletRequest request, AntPathMatcher pathMatcher) {
            return pathMatcher.match(pattern, request.getRequestURI());
        }
    }

    private static final List<AnyMethodPermitRequest> ANY_METHOD_PERMIT_REQUESTS = List.of(
            new AnyMethodPermitRequest("/api/v1/login"),
            new AnyMethodPermitRequest("/swagger-ui/**"),
            new AnyMethodPermitRequest("/v3/api-docs/**"),
            new AnyMethodPermitRequest("/api/v1/users/email"),
            new AnyMethodPermitRequest("/api/v1/users"),
            new AnyMethodPermitRequest("/firebase-messaging-sw.js"),
            new AnyMethodPermitRequest("/fcm-test.html"),
            new AnyMethodPermitRequest("/health"),
            new AnyMethodPermitRequest("/oauth2/authorization/github"),
            new AnyMethodPermitRequest("/api/v1/payments/webhook"),
            new AnyMethodPermitRequest("/api/v1/users/email/validation"),
            new AnyMethodPermitRequest("/api/v1/users/email/verification"),
            new AnyMethodPermitRequest("/api/v1/skills/**"),
            new AnyMethodPermitRequest("/api/v1/projects/roles")
    );

    private static final List<PermitRequest> METHOD_PERMIT_REQUESTS = List.of(
            new PermitRequest(HttpMethod.GET, "/api/v1/regions/**"),
            new PermitRequest(HttpMethod.GET, "/login/oauth2/code/github"),
            new PermitRequest(HttpMethod.GET, "/api/v1/projects/promotions"),
            new PermitRequest(HttpMethod.GET, "/api/v1/projects/promotions/*")
    );

    private static final String TOKEN_REISSUE_API = "/api/v1/reissue";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String accessToken = CookieUtil.getAccessTokenFromRequest(request);
        String refreshToken = CookieUtil.getRefreshTokenFromRequest(request);


        if (isAllowedRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isReissueRequest = pathMatcher.match(TOKEN_REISSUE_API, requestUri)
                && "POST".equals(request.getMethod());

        if (isReissueRequest) {
            handleTokenReissue(response, refreshToken);
            return;
        }

        if (refreshToken != null && tokenBlackListService.isAlreadyBlackListed(refreshToken)) {
            expireCookies(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "이미 로그아웃한 사용자의 토큰입니다.");
            return;

        }

        if (accessToken == null || accessToken.isEmpty()) {
            log.debug("access 토큰 없음, URI={}", requestUri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
            return;
        }


        try {
            if (jwtUtil.isExpired(accessToken)) {
                log.debug("JWT 토큰 만료됨");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.");
                return;
            }

            String email = jwtUtil.getEmail(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            log.debug("JWT 토큰 만료됨");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.");
            return;
        } catch (Exception e) {
            log.error("JWT 토큰 검증 실패", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return;
        }


        filterChain.doFilter(request, response);
    }

    private boolean isAllowedRequest(HttpServletRequest request) {
        return ANY_METHOD_PERMIT_REQUESTS.stream()
                .anyMatch(permitRequest -> permitRequest.matches(request, pathMatcher))
                || METHOD_PERMIT_REQUESTS.stream()
                .anyMatch(permitRequest -> permitRequest.matches(request, pathMatcher));
    }
    private void handleTokenReissue(
            HttpServletResponse response,
            String refreshToken
    ) throws IOException {
        if (refreshToken == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token이 필요합니다.");
            return;
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "전달받은 토큰은 Refresh Token이 아닙니다.");
            return;
        }

        if (jwtUtil.isExpired(refreshToken)) {
            expireCookies(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token이 만료되었습니다. 다시 로그인해주세요.");
            return;
        }

        if (tokenBlackListService.isAlreadyBlackListed(refreshToken)) {
            expireCookies(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "이미 로그아웃한 사용자의 토큰입니다.");
            return;
        }

        try {
            Long userId = jwtUtil.getUserId(refreshToken);
            String email = jwtUtil.getEmail(refreshToken);
            String reissuedAccess = jwtUtil.createAccess(userId, email);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.addHeader("access", reissuedAccess);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\": \"액세스 토큰이 재발급되었습니다.\"}");

        } catch (Exception e) {
            log.error("Token 재발급 실패", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 재발급에 실패했습니다.");
        }
    }

    private void expireCookies(HttpServletResponse response) {
        CookieUtil.expireCookie(response, "access", "/", true, true, "Strict");
        CookieUtil.expireCookie(response, "refresh", "/", true, true, "Strict");
    }
}