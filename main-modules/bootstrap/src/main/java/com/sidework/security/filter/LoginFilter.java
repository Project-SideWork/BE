package com.sidework.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.security.dto.AuthenticatedUserDetails;
import com.sidework.security.util.CookieUtil;
import com.sidework.security.util.JwtUtil;
import com.sidework.security.dto.LoginCommand;
import com.sidework.user.application.port.out.UserOutPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public LoginFilter(
            JwtUtil jwtUtil,
            CookieUtil cookieUtil,
            AuthenticationManager authenticationManager
    ) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;

        setAuthenticationManager(authenticationManager);

        setFilterProcessesUrl("/api/v1/login");
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("Attempting authentication...");
        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            LoginCommand loginRequest = new ObjectMapper().readValue(messageBody, LoginCommand.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password(), null);

            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read request body", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication)
            throws IOException, ServletException {
        log.info("Authentication successful...");

        AuthenticatedUserDetails userDetails = (AuthenticatedUserDetails) authentication.getPrincipal();
        String email = userDetails.getEmail();
        try {
            response.addHeader("access", jwtUtil.createAccess(email));
        } catch (Exception e) {
            log.error("Error generating JWT: ", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        response.addCookie(cookieUtil.createCookie("refresh", jwtUtil.createRefresh(email)));
        response.setStatus(HttpStatus.OK.value());
    }

}
