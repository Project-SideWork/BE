package com.sidework.user.application.port.out;

public record GithubInfoDto(Long githubId, String githubLoginName, String githubAccessToken) {
}
