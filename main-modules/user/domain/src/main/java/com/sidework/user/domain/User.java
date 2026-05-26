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

    private String githubLoginName;

    private String githubAccessToken;

    private String githubProfileUrl;

    private String email;

    private String name;

    private String nickname;

    private String password;

    private Integer age;

    private Long residenceRegionId;

    private UserType type;

    private Boolean isActive;

    public static User create(
            String email,
            String name,
            String nickname,
            String password,
            Integer age,
            Long residenceRegionId,
            UserType type
    ) {
        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .age(age)
                .residenceRegionId(residenceRegionId)
                .type(type)
                .isActive(true)
                .build();
    }
    // 탈퇴 처리
    public void deactivate() {
        this.isActive = false;
    }

    public void update(
        String email,
        String name,
        String nickname,
        Integer age,
        Long residenceRegionId
    ) {
        if (email != null) {
            this.email = email;
        }
        if (name != null) {
            this.name = name;
        }
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (age != null) {
            this.age = age;
        }
        if (residenceRegionId != null) {
            this.residenceRegionId = residenceRegionId;
        }
    }

    public void addGithubInfo(Long githubId, String githubLoginName, String encodedToken, String githubProfileUrl) {
        this.githubId = githubId;
        this.githubLoginName = githubLoginName;
        this.githubAccessToken = encodedToken;
        this.githubProfileUrl = githubProfileUrl;
    }
}
