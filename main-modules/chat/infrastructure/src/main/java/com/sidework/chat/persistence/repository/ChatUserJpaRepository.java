package com.sidework.chat.persistence.repository;

import com.sidework.chat.persistence.entity.ChatUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserJpaRepository extends JpaRepository<ChatUserEntity, Long> {

    @Modifying
    @Query("""
            UPDATE ChatUserEntity cu
            SET cu.lastReadChatId = :chatMessageId
            WHERE cu.userId = :userId AND cu.chatRoomId = :chatRoomId
            """)

    void updateLastRead(@Param("userId") Long userId,
                        @Param("chatRoomId") Long chatRoomId,
                        @Param("chatMessageId") Long chatMessageId);

}
