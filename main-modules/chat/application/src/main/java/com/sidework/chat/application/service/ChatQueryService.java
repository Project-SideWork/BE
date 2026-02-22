package com.sidework.chat.application.service;

import com.sidework.chat.application.adapter.ChatRecord;
import com.sidework.chat.application.port.in.ChatMessageQueryResult;
import com.sidework.chat.application.port.in.ChatQueryUseCase;
import com.sidework.chat.application.port.out.ChatMessageOutPort;
import com.sidework.chat.application.port.out.ChatMessagePage;
import com.sidework.chat.application.port.out.ChatRoomOutPort;
import com.sidework.chat.application.port.out.ChatUserOutPort;
import com.sidework.common.exception.InvalidCommandException;
import com.sidework.common.exception.ForbiddenAccessException;
import com.sidework.common.util.CursorUtil;
import com.sidework.common.util.CursorWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService implements ChatQueryUseCase {
    private final ChatUserOutPort chatUserRepository;
    private final ChatMessageOutPort chatMessageRepository;
    private final ChatRoomOutPort chatRoomRepository;

    @Override
    public ChatMessageQueryResult queryMessagesByChatRoomId(Long chatRoomId, String cursor) {
        CursorWrapper decoded = CursorUtil.decode(cursor);
        ChatMessagePage page = chatMessageRepository.findByChatRoomIdAndIdGreaterThan(chatRoomId,
                decoded.cursorCreatedAt(), decoded.cursorId(), 3);

        List<ChatRecord> records = page.items().stream().map(
                chatMessage -> ChatRecord.create(chatMessage.getId(), chatMessage.getContent(), chatMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")))
        ).toList();


        Instant nextCursorCreatedAt = page.nextCursorCreatedAt() != null
                ? page.nextCursorCreatedAt().atZone(ZoneOffset.UTC).toInstant()
                : null;


        String nextCursor = CursorUtil.encode(new CursorWrapper(nextCursorCreatedAt, page.nextCursorId()));

        return new ChatMessageQueryResult(
                records,
                nextCursor,
                page.hasNext()
        );
    }

    @Override
    public void checkSubscribeValidation(Long userId, Long chatRoomId) {
        if(!chatRoomRepository.existsById(chatRoomId)) {
            throw new InvalidCommandException(chatRoomId + "은 존재하지 않는 채팅방 ID 입니다.");
        }
        if(!chatUserRepository.existsByUserAndRoom(userId, chatRoomId)) {
            throw new ForbiddenAccessException();
        }
    }
}
