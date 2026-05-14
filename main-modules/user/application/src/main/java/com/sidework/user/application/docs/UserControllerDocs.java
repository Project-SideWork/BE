package com.sidework.user.application.docs;

import com.sidework.common.response.ApiResponse;
import com.sidework.user.application.adapter.EmailExistResponse;
import com.sidework.user.application.port.in.EmailCommand;
import com.sidework.user.application.port.in.SignUpCommand;
import com.sidework.user.application.port.in.VerificationCodeCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "유저 API")
public interface UserControllerDocs {

    @Operation(description = "이메일 중복 확인 후 인증번호 전송")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용 가능한 이메일이며 인증번호 전송 요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "200 예시",
                                    value = """
                                            {
                                              "code": "COMMON_200",
                                              "message": "성공입니다.",
                                              "isSuccess": true,
                                              "path": "/api/v1/users/email/validation"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "이메일 형식 오류 또는 요청 본문 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "올바른 이메일 형식이어야 합니다.",
                                              "isSuccess": false,
                                              "path": "/api/v1/users/email/validation"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 사용 중인 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "409 예시",
                                    value = """
                                            {
                                              "code": "USER_003",
                                              "message": "이미 사용 중인 이메일입니다.",
                                              "isSuccess": false,
                                              "path": "/api/v1/users/email/validation"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> checkEmailAvailableAndSendCode(
            @RequestBody @Valid EmailCommand command
    );


    @Operation(description = "이메일 인증번호 검증")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "인증번호 검증 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "200 예시",
                                    value = """
                                        {
                                          "code": "COMMON_200",
                                          "message": "성공입니다.",
                                          "isSuccess": true,
                                          "result": true
                                        }
                                        """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 또는 인증번호 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                        {
                                          "code": "COMMON_400",
                                          "message": "인증번호는 6자리 숫자여야 합니다.",
                                          "isSuccess": false,
                                          "path": "/api/v1/users/email/verification"
                                        }
                                        """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Boolean>> verifyEmailCode(
            @RequestBody @Valid VerificationCodeCommand command
    );

    @Operation(description = "회원가입")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "잘못된 요청입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> postNewUser(@RequestBody SignUpCommand command);
}
