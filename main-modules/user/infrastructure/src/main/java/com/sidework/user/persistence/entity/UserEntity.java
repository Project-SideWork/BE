package com.sidework.user.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import com.sidework.user.domain.UserType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // 실명
    @Column(name = "name", nullable = false, length = 15)
    private String name;

    // 서비스 내 노출 이름
    @Column(name = "nickname", nullable = false, length = 30)
    private String nickname;

    // 비밀번호 (암호화 저장 전제)
    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    private String password;

    // 만 나이
    @Column(name = "age", nullable = false)
    private Integer age;

    // 전화번호
    @Column(name = "tel", nullable = false, length = 20)
    private String tel;

    // 회원가입 종류 (LOCAL, KAKAO, GITHUB, GOOGLE)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private UserType type;

    // 활성 / 비활성 여부 (true = 활성, false = 탈퇴)
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}