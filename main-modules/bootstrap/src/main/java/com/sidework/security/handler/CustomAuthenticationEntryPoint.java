package com.sidework.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.onFailure(
                ErrorStatus.UNAUTHORIZED.getCode(),
                ErrorStatus.UNAUTHORIZED.getMessage(),
                null,
                request.getRequestURI()
        );

        response.getWriter().write(
                objectMapper.writeValueAsString(apiResponse)
        );
    }
}
