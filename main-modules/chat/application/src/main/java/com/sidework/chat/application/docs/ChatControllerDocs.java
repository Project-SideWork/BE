package com.sidework.chat.application.docs;

import com.sidework.chat.application.adapter.ChatRecord;
import com.sidework.chat.application.port.in.ExistChatCommand;
import com.sidework.chat.application.port.in.NewChatCommand;
import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.CursorResponse;
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

@Tag(name = "채팅 API")
public interface ChatControllerDocs {

    @Operation(description = "채팅방 SSE 구독")
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "403 예시",
                                    value = """
                                            {
                                              "code": "COMMON_403",
                                              "message": "권한이 부족합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    SseEmitter subscribe(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("chatRoomId") Long chatRoomId
    );

    @Operation(description = "새 채팅방 생성 및 첫 메시지 전송")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "전송 성공"),
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
            ),
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "403 예시",
                                    value = """
                                            {
                                              "code": "COMMON_403",
                                              "message": "권한이 부족합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> sendNewChatToCreateRoom(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @RequestBody NewChatCommand chatContent
    );

    @Operation(description = "기존 채팅방에 메시지 전송")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "전송 성공"),
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
            ),
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "403 예시",
                                    value = """
                                            {
                                              "code": "COMMON_403",
                                              "message": "권한이 부족합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> sendNewChatToExistRoom(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestBody ExistChatCommand chatContent
    );

    @Operation(description = "채팅 메시지 목록 조회")
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "403 예시",
                                    value = """
                                            {
                                              "code": "COMMON_403",
                                              "message": "권한이 부족합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<CursorResponse<ChatRecord>>> getMessages(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam(required = false, value = "cursor") String cursor
    );
}
