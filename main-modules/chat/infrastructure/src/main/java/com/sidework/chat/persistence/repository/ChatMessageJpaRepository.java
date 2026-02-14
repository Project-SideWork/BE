package com.sidework.chat.persistence.repository;

import com.sidework.chat.persistence.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageEntity, Long> {
}
