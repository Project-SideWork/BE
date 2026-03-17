package com.sidework.profile.persistence.entity;

import com.sidework.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_likes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileLikeEntity extends BaseEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "profile_id", nullable = false)
	private Long profileId;

	@Column(name = "user_id", nullable = false)
	private Long userId;
}
