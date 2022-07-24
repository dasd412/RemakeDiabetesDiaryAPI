/*
 * @(#)PrincipalDetails.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config.security.auth;

import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Authentication 객체에 넣기 위한 래퍼 객체.
 * 사용자 정의 UserDetails 와 OAuth 로그인용 OAuth2User 를 모두 implements 하였기 때문에, Authentication 객체에 두 타입 모두로 인식가능해졌다.
 */
public class PrincipalDetails implements UserDetails, OAuth2User {

    /**
     * 실제 작성자 엔티티를 참조해야 한다. 기본 로그인 방식과 OAuth 로그인 방식 모두에 쓰인다.
     */
    private final Writer writer;

    /**
     * OAuth와 관련된 속성을 담고 있는 해시맵. OAuth 로그인시에만 쓰인다.
     */
    private Map<String, Object> oauthAttributes;

    /**
     * 기본 로그인 방식인 Form Login 시 사용되는 생성자
     */
    public PrincipalDetails(Writer writer) {
        this.writer = writer;
    }

    /**
     * OAuth 인증 시 사용되는 생성자
     *
     * @param writer          실제 작성자 엔티티
     * @param oauthAttributes OAuth와 관련된 속성을 담고 있는 해시맵.
     */
    public PrincipalDetails(Writer writer, Map<String, Object> oauthAttributes) {
        this.writer = writer;
        this.oauthAttributes = oauthAttributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> writer.getRole().name());
        return collection;
    }

    public Writer getWriter() {
        return writer;
    }

    /**
     * PrincipalDetailsService 또는 PrincipalOAuth2UserService의 loadUser() 호출 시 스프링 시큐리티에서 '알아서' 이 비밀번호를 체크한다.
     */
    @Override
    public String getPassword() {
        return writer.getPassword();
    }

    @Override
    public String getUsername() {
        return writer.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.oauthAttributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
