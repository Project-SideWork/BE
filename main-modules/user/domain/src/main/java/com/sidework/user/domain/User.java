package com.sidework.user.domain;

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

    public User(
            String email,
            String name,
            String nickname,
            String password,
            Integer age,
            String tel,
            UserType type
    ) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.age = age;
        this.tel = tel;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    // 탈퇴 처리
    public void deactivate() {
        this.isActive = false;
    }
}
