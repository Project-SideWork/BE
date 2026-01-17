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

    private UserType type;

    private Boolean isActive = true;

    public static User create(
            String email,
            String name,
            String nickname,
            String password,
            Integer age,
            String tel,
            UserType type
    ) {
        return new User(null, email, name, nickname, password, age, tel, type, true);
    }
    // 탈퇴 처리
    public void deactivate() {
        this.isActive = false;
    }
}
