package com.sidework.chat.persistence.entity;


import com.sidework.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_message")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;

    private Long senderId;

    private String content;

    private Boolean isDeleted;
}
