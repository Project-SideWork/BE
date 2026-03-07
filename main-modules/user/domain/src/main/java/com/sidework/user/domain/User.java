package com.sidework.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    private String email;

    private String name;

    private String nickname;

    private String password;

    private Integer age;

    private String tel;

    private Long residenceRegionId;

    private UserType type;

    private Boolean isActive = true;

    public static User create(
            String email,
            String name,
            String nickname,
            String password,
            Integer age,
            String tel,
            Long residenceRegionId,
            UserType type
    ) {
        return new User(null, email, name, nickname, password, age, tel, residenceRegionId, type, true);
    }
    // 탈퇴 처리
    public void deactivate() {
        this.isActive = false;
    }
}
