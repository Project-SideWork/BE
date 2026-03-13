package com.sidework.security.oauth.user;

import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
public class GithubOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final String accessToken;
    private final String id;
    private final String name;
    private final String profileUrl;

    public GithubOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        this.accessToken = accessToken;
        this.attributes = attributes;

        Object rawId = attributes.get("id");
        this.id = Objects.requireNonNull(rawId, "GitHub user id is missing").toString();

        // name이 없을 경우 login(username)으로 대체
        this.name = attributes.get("name") != null
                ? (String) attributes.get("name")
                : (String) attributes.get("login");
        this.profileUrl = (String) attributes.get("avatar_url");
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.GITHUB;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProfileImage() {
        return profileUrl;
    }
}
