package com.sidework.notification.application.docs;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.CursorResponse;
import com.sidework.notification.application.adapter.FcmTestRequest;
import com.sidework.notification.application.adapter.FcmTokenRegisterRequest;
import com.sidework.notification.application.adapter.NotificationResponse;
import com.sidework.notification.application.port.in.NotificationCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림 API")
public interface NotificationControllerDocs {

    @Operation(description = "알림 SSE 구독")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "구독 성공"),
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
    SseEmitter subscribe(@AuthenticationPrincipal AuthenticatedUserDetails user);

    @Operation(description = "알림 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
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
    ResponseEntity<ApiResponse<CursorResponse<NotificationResponse>>> list(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @RequestParam(required = false, name = "cursor") String cursor,
            @RequestParam(required = false, name = "size", defaultValue = "10") int size
    );

    @Operation(description = "알림 읽음 처리")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "알림 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "404 예시",
                                    value = """
                                            {
                                              "code": "COMMON_404",
                                              "message": "알림을 찾을 수 없습니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal AuthenticatedUserDetails user
    );

    @Operation(description = "테스트 알림 생성")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전송 성공")
    })
    ResponseEntity<ApiResponse<Void>> sendTest(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @RequestBody NotificationCommand request
    );

    @Operation(description = "FCM 토큰 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공"),
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
    ResponseEntity<ApiResponse<Void>> registerFcmToken(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @RequestBody FcmTokenRegisterRequest request
    );

    @Operation(description = "FCM 테스트 푸시 발송")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "발송 성공")
    })
    ResponseEntity<ApiResponse<Void>> sendFcmTest(@RequestBody(required = false) FcmTestRequest request);
}
