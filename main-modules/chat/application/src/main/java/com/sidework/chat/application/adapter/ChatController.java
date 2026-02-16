package com.sidework.chat.application.adapter;

import com.sidework.chat.application.port.in.ChatCommandUseCase;
import com.sidework.chat.application.port.in.ChatMessageQueryResult;
import com.sidework.chat.application.port.in.ChatQueryUseCase;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatCommandUseCase chatCommandService;
    private final ChatQueryUseCase chatQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendNewChat(@RequestBody ChatContent chatContent) {
        chatCommandService.processStartNewChat(chatContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<CursorResponse<ChatRecord>>> getMessages(@PathVariable("chatRoomId") Long chatRoomId,
                                                                               @RequestParam(required = false, value = "cursor") String cursor) {
        ChatMessageQueryResult queryRes = chatQueryService.queryByChatRoomId(chatRoomId, cursor);
        CursorResponse<ChatRecord> res = new CursorResponse<>(queryRes.items(), queryRes.nextCursor(), queryRes.hasNext());
        return ResponseEntity.ok().body(ApiResponse.onSuccess(res));
    }
}
