package com.sidework.chat.application.adapter;

import com.sidework.chat.application.port.in.*;
import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.event.sse.port.in.SseSubscribeUseCase;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatCommandUseCase chatCommandService;
    private final ChatQueryUseCase chatQueryService;
    private final SseSubscribeUseCase sseSubscribeUseCase;

    @GetMapping("/subscribe/{chatRoomId}")
    public SseEmitter subscribe(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("chatRoomId") Long chatRoomId) {
        chatQueryService.checkChatUserValidation(user.getId(), chatRoomId);
        return sseSubscribeUseCase.subscribeChat(user.getId(), chatRoomId);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendNewChatToCreateRoom(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @Validated @RequestBody NewChatCommand chatContent) {
        chatCommandService.processStartNewChat(user.getId(), chatContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> sendNewChatToExistRoom(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("chatRoomId") Long chatRoomId,
            @Validated @RequestBody ExistChatCommand chatContent) {
        chatCommandService.processResumeChat(chatRoomId, user.getId(), chatContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<CursorResponse<ChatRecord>>> getMessages(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam(required = false, value = "cursor") String cursor) {
        chatQueryService.checkChatUserValidation(user.getId(), chatRoomId);
        ChatMessageQueryResult queryRes = chatQueryService.queryMessagesByChatRoomId(chatRoomId, cursor);
        CursorResponse<ChatRecord> res = new CursorResponse<>(queryRes.items(), queryRes.nextCursor(), queryRes.hasNext());
        return ResponseEntity.ok().body(ApiResponse.onSuccess(res));
    }
}
