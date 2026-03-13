package com.sidework.security.docs;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.security.dto.LoginCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Tag(name = "인증 API")
public interface AuthControllerDocs {

    @Operation(description = "로그인")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    void login(@RequestBody LoginCommand command);

    @Operation(description = "로그아웃")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    void logout();

    @Operation(description = "쿠키를 매개로 토큰 재발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    void reissueToken();

    @Operation(description = "GitHub 계정 연동")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "302", description = "GitHub 로그인 페이지로 리다이렉트"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    void linkGithub(
            HttpServletRequest request, HttpServletResponse response) throws IOException;
}
