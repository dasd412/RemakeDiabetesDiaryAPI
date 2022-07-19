/*
 * @(#)PrincipalOAuth2UserService.java        1.0.8 2022/2/16
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config.security.oauth;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.config.security.oauth.provider.OAuth2UserInfo;
import com.dasd412.remake.api.config.security.oauth.provider.OAuth2UserInfoFactory;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.dasd412.remake.api.service.security.WriterService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * "/login" 요청 시 인터셉트했는데 OAuth 로그인인 경우, OAuth2User 를 Authentication 객체에 넣어주는 서비스 클래스.
 *
 * @author 양영준
 * @version 1.0.8 2022년 2월 16일
 */
@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    /**
     * 이미 WriterService 내에 회원 가입 로직이 있기 때문에 재사용한다.
     */
    private final WriterService writerService;
    /**
     * 기존의 동일한 회원이 존재하는 지 파악하기 위한 리포지토리
     */
    private final WriterRepository writerRepository;

    /**
     * 어떤 OAuth2 user 정보인지 알려주는 팩토리
     */
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    public PrincipalOAuth2UserService(WriterService writerService, WriterRepository writerRepository, OAuth2UserInfoFactory oAuth2UserInfoFactory) {
        this.writerService = writerService;
        this.writerRepository = writerRepository;
        this.oAuth2UserInfoFactory = oAuth2UserInfoFactory;
    }

    /**
     * OAuth 로그인 시 호출되는 메서드.
     *
     * @param oAuth2UserRequest OAuth 로그인 요청이 담겨있다.
     * @return OAuth 로그인의 경우 OAuth2User 구현체를 리턴해야만 스프링 시큐리티가 관리할 수 있다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        /* OAuth 로그인의 경우 OAuth Provider 정보가 필요하다. */
        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory.selectOAuth2UserInfo(oAuth2User, oAuth2UserRequest).orElseThrow(() -> new IllegalStateException("등록된 provider 가 아닙니다."));

        String username = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();

        /* OAuth 로그인의 경우 비밀번호는 사용되지 않는다. */
        String password = "diabetesdiaryapi";

        Role role = Role.User;

        /* 만약 회원가입된 적 없으면, writerService 한테 회원 가입을 요청한다. 비밀 번호 인코딩도 해준다. */
        Writer writer = writerRepository.findWriterByName(username)
                .orElseGet(() ->
                        writerService.saveWriterWithSecurity(username,
                                oAuth2UserInfo.getEmail(),
                                password,
                                role,
                                oAuth2UserInfo.getProvider(),
                                oAuth2UserInfo.getProviderId()));

        /* 리턴 객체는 스프링 시큐리티 세션 내의 Authentication 에 담기게 된다. */
        return new PrincipalDetails(writer, oAuth2User.getAttributes());
    }

}
