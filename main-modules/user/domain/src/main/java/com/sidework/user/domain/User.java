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

    private Long githubId;

    private String githubAccessToken;

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
        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .age(age)
                .tel(tel)
                .residenceRegionId(residenceRegionId)
                .type(type)
                .build();
    }
    // 탈퇴 처리
    public void deactivate() {
        this.isActive = false;
    }

    public void addGithubInfo(Long githubId, String githubAccessToken) {
        this.githubId = githubId;
        this.githubAccessToken = githubAccessToken;
    }
}
