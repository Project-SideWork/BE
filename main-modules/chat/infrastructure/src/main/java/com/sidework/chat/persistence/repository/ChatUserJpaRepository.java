package com.sidework.chat.persistence.repository;

import com.sidework.chat.application.port.out.ChatUserSummary;
import com.sidework.chat.persistence.entity.ChatUserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChatUserJpaRepository extends JpaRepository<ChatUserEntity, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE ChatUserEntity cu
            SET cu.lastReadChatId =
                CASE WHEN cu.lastReadChatId IS NULL THEN :chatMessageId
                    WHEN cu.lastReadChatId < :chatMessageId THEN :chatMessageId
                    ELSE cu.lastReadChatId
                END
            WHERE cu.userId = :userId AND cu.chatRoomId = :chatRoomId
            """)

    int updateLastRead(@Param("userId") Long userId,
                        @Param("chatRoomId") Long chatRoomId,
                        @Param("chatMessageId") Long chatMessageId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
    UPDATE ChatUserEntity cu
       SET cu.isConnected = :connected
     WHERE cu.userId = :userId
       AND cu.chatRoomId = :chatRoomId
    """)
    void updateIsConnected(
            @Param("userId") Long userId,
            @Param("chatRoomId") Long chatRoomId,
            @Param("connected") boolean connected
    );


    @Query("""
               SELECT count(cu) > 0 FROM ChatUserEntity cu
               WHERE cu.userId = :userId and cu.chatRoomId = :chatRoomId
            """)
    boolean existsByUserAndChatRoom(@Param("userId") Long userId,
                                    @Param("chatRoomId") Long chatRoomId);

    @Query("""
            SELECT cu.isConnected FROM ChatUserEntity cu
            WHERE cu.userId = :userId and cu.chatRoomId = :chatRoomId
            """)
    boolean findConnectedByUserAndChatRoom(@Param("userId") Long userId,
                                           @Param("chatRoomId") Long chatRoomId);


    @Query("""
            SELECT cu.userId FROM ChatUserEntity cu
            WHERE cu.userId != :userId and cu.chatRoomId = :chatRoomId
            """)
    Long findChatPair(@Param("userId") Long userId,
                                           @Param("chatRoomId") Long chatRoomId);

    @Query("""
    select new com.sidework.chat.application.port.out.ChatUserSummary(
            cr.id,
            cr.lastMessageContent,
            cr.lastMessageSentTime,
            count(cm.id),
            cr.createdAt
        )
        from ChatUserEntity cu
        join ChatRoomEntity cr
            on cu.chatRoomId = cr.id
        left join ChatMessageEntity cm
            on cm.chatRoomId = cr.id
           and cm.id > coalesce(cu.lastReadChatId, 0)
           and cm.senderId <> cu.userId
           and cm.isDeleted = false
        where cu.userId = :userId
          and (
                :cursorSentTime is null
             or cr.lastMessageSentTime < :cursorSentTime
             or (cr.lastMessageSentTime = :cursorSentTime and cr.id < :cursorRoomId)
          )
        group by cr.id, cr.lastMessageContent, cr.lastMessageSentTime, cr.createdAt
        order by cr.lastMessageSentTime desc, cr.id desc
    """)
    List<ChatUserSummary> findChatUserSummary(
            @Param("userId") Long userId,
            @Param("cursorSentTime") Instant cursorSentTime,
            @Param("cursorRoomId") Long cursorRoomId,
            Pageable pageable
    );
}
