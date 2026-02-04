package com.sidework.notification.persistence.entity;

import java.time.LocalDateTime;


import com.sidework.common.entity.BaseEntity;
import com.sidework.notification.domain.NotificationType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "type", nullable = false, length = 50)
	private NotificationType type;

	@Column(name = "title", nullable = false, length = 200)
	private String title;

	@Column(name = "body", columnDefinition = "TEXT")
	private String body;

	@Column(name = "is_read", nullable = false)
	private boolean read;

}
