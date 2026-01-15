package com.sidework.user.application.adapter;

public record SignUpCommand(
        String email,
        String password,
        String name,
        String nickname,
        int age,
        String tel
) {
}
