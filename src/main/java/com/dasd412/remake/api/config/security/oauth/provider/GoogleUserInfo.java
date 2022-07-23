/*
 * @(#)GoogleUserInfo.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config.security.oauth.provider;

import java.util.Map;

/**
 * OAuth provider가 google인 경우의 사용자 정보.
 */

public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return 구글의 provider 값은 "google"
     */
    @Override
    public String getProvider() {
        return "google";
    }

    /**
     * @return 구글의 경우 식별자는 "sub"
     */
    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
