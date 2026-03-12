package com.sidework.security.oauth.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class GithubOAuth2UserUnlink implements OAuth2UserUnlink {

    // DELETE https://api.github.com/applications/{client_id}/token
    private static final String URL = "https://api.github.com/applications/{clientId}/token";

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Override
    public void unlink(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        // GitHub API는 Basic Auth (clientId:clientSecret) 로 앱 인증
        headers.setBasicAuth(clientId, clientSecret);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        // 삭제할 토큰을 body에 담아 DELETE 요청
        String body = "{\"access_token\":\"" + accessToken + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        restTemplate.exchange(
                URL,
                HttpMethod.DELETE,
                entity,
                Void.class,
                clientId
        );
    }
}

