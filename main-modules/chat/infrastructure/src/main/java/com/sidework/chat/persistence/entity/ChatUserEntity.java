package com.sidework.chat.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;

    private Long userId;

    private Long lastReadChatId;
}
