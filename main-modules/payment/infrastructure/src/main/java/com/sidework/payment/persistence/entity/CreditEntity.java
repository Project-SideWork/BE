package com.sidework.payment.persistence.entity;


import com.sidework.common.entity.BaseEntity;
import com.sidework.payment.domain.CreditType;
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
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditType type;

    @Column(length = 255)
    private String description;

    @Column(length = 100)
    private Long referenceId;

    private Instant expiredAt;
}