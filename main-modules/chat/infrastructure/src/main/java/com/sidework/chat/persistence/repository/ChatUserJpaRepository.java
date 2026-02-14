package com.sidework.chat.persistence.repository;

import com.sidework.chat.persistence.entity.ChatUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserJpaRepository extends JpaRepository<ChatUserEntity, Long> {
}
