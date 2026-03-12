package com.sidework.security.oauth.user;

import lombok.Getter;

import java.util.Map;

@Getter
public class GithubOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final String accessToken;
    private final String id;
    private final String email;
    private final String name;
    private final String profileUrl;

    public GithubOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        this.accessToken = accessToken;
        this.attributes = attributes;
        // GitHub은 숫자 id를 Integer로 반환하므로 String 변환
        this.id = String.valueOf(attributes.get("id"));
        this.email = (String) attributes.get("email");
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
    public String getEmail() {
        return email;
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
