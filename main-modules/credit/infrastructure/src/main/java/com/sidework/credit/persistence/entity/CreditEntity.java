package com.sidework.credit.persistence.entity;


import com.sidework.common.entity.BaseEntity;
import com.sidework.credit.domain.CreditType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "credits")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Integer remainingAmount;

    private Instant expiresAt;
}