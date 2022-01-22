/*
 * @(#)GitHubUserInfo.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.config.security.oauth.provider;

import java.util.Map;

/**
 * OAuth provider가 github인 경우의 사용자 정보.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

public class GitHubUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GitHubUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return 깃헙의 경우 provider는 "github"
     */
    @Override
    public String getProvider() {
        return "github";
    }

    /**
     * @return 깃헙 사용자 식별 정보는 "id"
     */
    @Override
    public String getProviderId() {
        int id = (int) attributes.get("id");
        return String.valueOf(id);
    }

    /**
     * 깃헙의 경우 email이 필수가 아니다. 따라서 null이 될 수 가 있다.
     *
     * @return 이메일이 없을 경우? 임의로 조합 : 있으면 이메일 그대로 리턴.
     */
    @Override
    public String getEmail() {
        int id = (int) attributes.get("id");
        return attributes.get("email") == null ? id + "@github.com" : (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("login");
    }
}
