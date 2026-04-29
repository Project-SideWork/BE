package com.sidework.chat.application.service;

import com.sidework.chat.application.adapter.ChatRecord;
import com.sidework.chat.application.adapter.ChatRoomRecord;
import com.sidework.chat.application.port.in.ChatMessageQueryResult;
import com.sidework.chat.application.port.in.ChatQueryUseCase;
import com.sidework.chat.application.port.in.ChatRoomQueryResult;
import com.sidework.chat.application.port.out.*;
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

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    @Transactional(readOnly = false)
    public ChatMessageQueryResult queryMessagesByChatRoomId(Long chatRoomId, Long userId, String cursor) {
        CursorWrapper decoded = CursorUtil.decode(cursor);
        ChatMessagePage page = chatMessageRepository.findByChatRoomIdAndIdGreaterThan(chatRoomId,
                decoded.cursorCreatedAt(), decoded.cursorId(), 3);

        Long lastChatId = page.items().getFirst().getId();

        List<ChatRecord> records = page.items().stream().map(
                chatMessage -> ChatRecord.create(chatMessage.getId(), chatMessage.getSenderId(), chatMessage.getContent(), chatMessage.getSendTime().format(TIME_FORMATTER))
        ).toList();

        chatUserRepository.updateLastReadChat(userId, chatRoomId, lastChatId);


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
    public ChatRoomQueryResult queryRoomsByUserId(Long userId, String cursor) {
        CursorWrapper decoded = CursorUtil.decode(cursor);
        ChatUserSummaryPage page = chatUserRepository.findByUserIdAndIdGreaterThan(userId,decoded.cursorCreatedAt(), decoded.cursorId(), 3);


        List<ChatRoomRecord> records = page.items().stream().map(
                summary -> ChatRoomRecord.create(summary.chatRoomId(), summary.lastMessageContent(),
                        summary.lastMessageSentTime().toString(), summary.unreadCount())
        ).toList();


        Instant nextCursorCreatedAt = page.nextCursorCreatedAt() != null
                ? page.nextCursorCreatedAt().atZone(ZoneOffset.UTC).toInstant()
                : null;


        String nextCursor = CursorUtil.encode(new CursorWrapper(nextCursorCreatedAt, page.nextCursorId()));

        return new ChatRoomQueryResult(
                records,
                nextCursor,
                page.hasNext()
        );
    }

    @Override
    public void checkChatUserValidation(Long userId, Long chatRoomId) {
        if(!chatRoomRepository.existsById(chatRoomId)) {
            throw new InvalidCommandException(chatRoomId + "은 존재하지 않는 채팅방 ID 입니다.");
        }
        if(!chatUserRepository.existsByUserAndRoom(userId, chatRoomId)) {
            throw new ForbiddenAccessException();
        }
    }
}
