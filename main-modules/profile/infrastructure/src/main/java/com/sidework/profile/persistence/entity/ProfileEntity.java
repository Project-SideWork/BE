package com.sidework.profile.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "self_introduction", columnDefinition = "TEXT")
	private String selfIntroduction;

	@Column(name = "residence", length = 20)
	private String residence;
}

