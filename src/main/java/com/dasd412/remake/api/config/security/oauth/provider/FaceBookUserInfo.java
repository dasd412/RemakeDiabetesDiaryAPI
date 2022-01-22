/*
 * @(#)FaceBookUserInfo.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config.security.oauth.provider;

import java.util.Map;

/**
 * OAuth provider가 facebook인 경우의 사용자 정보.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */


public class FaceBookUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public FaceBookUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return 페이스북 provider 는 "facebook"
     */
    @Override
    public String getProvider() {
        return "facebook";
    }

    /**
     * @return facebook의 경우 식별자는 "id"
     */
    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
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
