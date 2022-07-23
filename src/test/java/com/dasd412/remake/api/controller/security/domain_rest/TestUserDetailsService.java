/*
 * @(#)TestUserDetailsService.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_rest;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;

/**
 * 가짜 UserDetails를 공급해주는 서비스. 테스트 수행시에만 사용된다.
 * 만약 @Service 적용하면 중복 빈 주입 오류 난다.
 */
@Profile("test")
public class TestUserDetailsService implements UserDetailsService {

    public static final String USERNAME = "user@example.com";
    public static final String OAUTH_USER_NAME = "test@google.com";

    /**
     * @return 가짜 사용자 인증 정보
     */
    private Writer getUserMock() {
        return Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(USERNAME)
                .email(USERNAME)
                .password("test")
                .provider(null)
                .providerId(null)
                .role(Role.User)
                .build();
    }

    /**
     * @return 가짜 OAuth 유저 인증 정보
     */
    private Writer getOAuthUserMock() {
        return Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(OAUTH_USER_NAME)
                .email(OAUTH_USER_NAME)
                .password(null)
                .provider("google")
                .providerId("1")
                .role(Role.User)
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        if (userName.equals(USERNAME)) {
            return new PrincipalDetails(getUserMock());
        } else if (userName.equals(OAUTH_USER_NAME)) {
            return new PrincipalDetails(getOAuthUserMock(), new HashMap<>());
        }
        return null;
    }
}
