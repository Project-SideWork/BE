package com.sidework.chat.persistence.repository;

import com.sidework.chat.persistence.entity.ChatMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query(
            """
            select cm from ChatMessageEntity cm
            where (cm.chatRoomId = :chatRoomId)
              and (
                    :cursorCreatedAt is null
                 or cm.createdAt < :cursorCreatedAt
                 or (cm.createdAt = :cursorCreatedAt and cm.id < :cursorId)
              )
            order by cm.createdAt desc, cm.id desc
            """
    )
    List<ChatMessageEntity> pageBy(@Param("chatRoomId") Long chatRoomId,
                                   @Param("cursorCreatedAt") Instant cursorCreatedAt,
                                   @Param("cursorId") Long cursorId,
                                   Pageable pageable) ;
}
