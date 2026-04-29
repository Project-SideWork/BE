package com.sidework.chat.persistence.repository;

import com.sidework.chat.persistence.entity.ChatRoomEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
            UPDATE ChatRoomEntity cr
            SET cr.lastMessageContent = :content, cr.lastMessageSentTime = :sendTime,
            cr.lastMessageId = :lastMessageId, cr.lastMessageSenderId = :lastMessageSenderId
            WHERE cr.id = :chatRoomId
            """
    )
    int updateLastMessage(
            @Param("content") String messageContent,
            @Param("sendTime") Instant messageSendTime,
            @Param("lastMessageId") Long lastMessageId,
            @Param("lastMessageSenderId")Long lastMessageSenderId,
            @Param("chatRoomId") Long chatRoomId
    );
}
