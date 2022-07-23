/*
 * @(#)OAuth2UserInfo.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config.security.oauth.provider;

/**
 * OAuth 요청이 오면, 사용자가 어떤 provider를 통해 로그인했는지 알기 위해 사용되는 인터페이스.
 */
public interface OAuth2UserInfo {
    /**
     * @return Oauth 제공 회사 이름
     */
    String getProvider();

    /**
     * @return OAuth provider 가 제공한 "유일한" 식별자 값
     */
    String getProviderId();//

    String getEmail();

    String getName();
}

