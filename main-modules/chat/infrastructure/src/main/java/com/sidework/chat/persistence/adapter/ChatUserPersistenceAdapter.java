package com.sidework.chat.persistence.adapter;

import com.sidework.chat.application.port.out.ChatUserOutPort;
import com.sidework.chat.application.port.out.ChatUserSummary;
import com.sidework.chat.application.port.out.ChatUserSummaryPage;
import com.sidework.chat.persistence.entity.ChatUserEntity;
import com.sidework.chat.persistence.mapper.ChatUserMapper;
import com.sidework.chat.persistence.repository.ChatUserJpaRepository;
import com.sidework.domain.ChatUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatUserPersistenceAdapter implements ChatUserOutPort {
    private final ChatUserJpaRepository repo;
    private final ChatUserMapper mapper;
    @Override
    public void save(ChatUser chatUser) {
        ChatUserEntity entity = mapper.toEntity(chatUser);
        repo.save(entity);
    }

    @Override
    public int updateLastReadChat(Long userId, Long chatRoomId, Long chatMessageId) {
        return repo.updateLastRead(userId, chatRoomId, chatMessageId);
    }

    @Override
    public boolean existsByUserAndRoom(Long userId, Long chatRoomId) {
        return repo.existsByUserAndChatRoom(userId, chatRoomId);
    }

    @Override
    public boolean isChatRoomConnected(Long userId, Long chatRoomId) {
        return repo.findConnectedByUserAndChatRoom(userId, chatRoomId);
    }

    @Override
    public Long findChatPairInRoom(Long senderId, Long chatRoomId) {
        return repo.findChatPair(senderId, chatRoomId);
    }

    @Override
    public ChatUserSummaryPage findByUserIdAndIdGreaterThan(Long userId, Instant cursorCreatedAt, Long cursorId, int size) {
        List<ChatUserSummary> summaries = repo.findChatUserSummary(userId, cursorCreatedAt , cursorId, PageRequest.of(0, size));
        boolean hasNext = summaries.size() > size;
        ChatUserSummary last = hasNext && !summaries.isEmpty()
                ? summaries.getLast()
                : null;

        return new ChatUserSummaryPage(
                summaries,
                hasNext,
                last != null
                        ? LocalDateTime.ofInstant(Instant.from(last.createdAt()), ZoneOffset.UTC)
                        : null,
                last != null ? last.chatRoomId() : null
        );
    }

}
