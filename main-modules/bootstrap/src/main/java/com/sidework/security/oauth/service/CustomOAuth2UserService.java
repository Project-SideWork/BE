package com.sidework.security.oauth.service;

import com.sidework.security.oauth.exception.OAuth2AuthenticationProcessingException;
import com.sidework.security.oauth.user.OAuth2UserInfo;
import com.sidework.security.oauth.user.OAuth2UserInfoFactory;
import com.sidework.user.application.port.out.UserOutPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private OAuth2UserRequest oAuth2UserRequest;
    private final UserOutPort userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationProcessingException {
        log.info("🔍 OAuth2UserService: 사용자 정보 요청 시작");
        this.oAuth2UserRequest = oAuth2UserRequest;

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        log.info("🔍 OAuth2UserService: 사용자 정보 로드 완료 -> {}", oAuth2User.getAttributes());

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String accessToken = userRequest.getAccessToken().getTokenValue();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, accessToken, oAuth2User.getAttributes()
        );

        return new OAuth2UserPrincipal(oAuth2UserInfo);
    }
}