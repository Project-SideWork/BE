package com.sidework.chat.application.adapter;

import com.sidework.chat.application.port.in.*;
import com.sidework.chat.application.docs.ChatControllerDocs;
import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.event.sse.port.in.SseSubscribeUseCase;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatController implements ChatControllerDocs {
    private final ChatCommandUseCase chatCommandService;
    private final ChatQueryUseCase chatQueryService;
    private final SseSubscribeUseCase sseSubscribeUseCase;

    @GetMapping(
            value = "/subscribe/{chatRoomId}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter subscribe(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("chatRoomId") Long chatRoomId) {
        chatQueryService.checkChatUserValidation(user.getId(), chatRoomId);
        chatCommandService.enterChatRoom(user.getId(), chatRoomId);
        return sseSubscribeUseCase.subscribeChat(chatRoomId);
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
        ChatMessageQueryResult queryRes = chatQueryService.queryMessagesByChatRoomId(chatRoomId, user.getId(), cursor);
        CursorResponse<ChatRecord> res = new CursorResponse<>(queryRes.items(), queryRes.nextCursor(), queryRes.hasNext());
        return ResponseEntity.ok().body(ApiResponse.onSuccess(res));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CursorResponse<ChatRoomRecord>>> getChatRooms(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @RequestParam(required = false, value = "cursor") String cursor) {
        ChatRoomQueryResult queryRes = chatQueryService.queryRoomsByUserId(user.getId(), cursor);
        CursorResponse<ChatRoomRecord> res = new CursorResponse<>(queryRes.items(), queryRes.nextCursor(), queryRes.hasNext());
        return ResponseEntity.ok().body(ApiResponse.onSuccess(res));
    }

    @PatchMapping("/{chatRoomId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveChatRoom(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("chatRoomId") Long chatRoomId) {
        chatCommandService.leaveChatRoom(user.getId(), chatRoomId);
        return ResponseEntity.ok().body(ApiResponse.onSuccessVoid());
    }
}
