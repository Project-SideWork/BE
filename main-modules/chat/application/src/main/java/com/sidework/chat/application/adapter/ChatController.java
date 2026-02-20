package com.sidework.chat.application.adapter;

import com.sidework.chat.application.port.in.ChatCommandUseCase;
import com.sidework.chat.application.port.in.ChatMessageQueryResult;
import com.sidework.chat.application.port.in.ChatQueryUseCase;
import com.sidework.common.event.sse.port.in.SseSubscribeUseCase;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(value = "/subscribe/{chatRoomId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("chatRoomId") Long chatRoomId) {
        return sseSubscribeUseCase.subscribeChat(chatRoomId);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendNewChat(@Validated @RequestBody NewChatCommand chatContent) {
        chatCommandService.processStartNewChat(chatContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> sendNewChatToExistRoom(@PathVariable("chatRoomId") Long chatRoomId,
                                                                    @Validated @RequestBody ExistChatCommand chatContent) {
        chatCommandService.processExistChat(chatRoomId, chatContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<CursorResponse<ChatRecord>>> getMessages(@PathVariable("chatRoomId") Long chatRoomId,
                                                                               @RequestParam(required = false, value = "cursor") String cursor) {
        ChatMessageQueryResult queryRes = chatQueryService.queryMessagesByChatRoomId(chatRoomId, cursor);
        CursorResponse<ChatRecord> res = new CursorResponse<>(queryRes.items(), queryRes.nextCursor(), queryRes.hasNext());
        return ResponseEntity.ok().body(ApiResponse.onSuccess(res));
    }
}
