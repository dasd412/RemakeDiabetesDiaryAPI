/*
 * @(#)OAuth2UserInfoFactory.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config.security.oauth.provider;


import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * registrationId를 읽어서 어떤 OAuth2User인지 알려주는 팩토리.
 */
@Component
public class OAuth2UserInfoFactory {

    public Optional<OAuth2UserInfo> selectOAuth2UserInfo(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        switch (registrationId) {
            case "google":
                return Optional.of(new GoogleUserInfo(oAuth2User.getAttributes()));
            case "facebook":
                return Optional.of(new FaceBookUserInfo(oAuth2User.getAttributes()));
            case "github":
                return Optional.of(new GitHubUserInfo(oAuth2User.getAttributes()));
            default:
                return Optional.empty();
        }
    }
}
