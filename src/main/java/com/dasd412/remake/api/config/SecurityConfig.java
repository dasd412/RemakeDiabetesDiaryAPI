/*
 * @(#)SecurityConfig.java        1.1.1 2022/2/26
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config;

import com.dasd412.remake.api.config.security.AuthenticationExceptionJudge;
import com.dasd412.remake.api.config.security.LoginFailHandler;
import com.dasd412.remake.api.config.security.oauth.PrincipalOAuth2UserService;
import com.dasd412.remake.api.domain.diary.writer.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 웹 시큐리티와 관련된 설정용 클래스.
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 26일
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * OAuth 2.0과 관련된 작업을 하는 서비스 객체
     */
    private final PrincipalOAuth2UserService principalOAuth2UserService;

    /**
     * 로그인 실패시 예외를 판단하여, 적절한 예외 메시지를 던져주는 객체
     */
    private final AuthenticationExceptionJudge judge;

    public SecurityConfig(PrincipalOAuth2UserService principalOAuth2UserService, AuthenticationExceptionJudge judge) {
        this.principalOAuth2UserService = principalOAuth2UserService;
        this.judge = judge;
    }

    /**
     * h2 데이터베이스 접근 시에는 스프링 시큐리티 필터를 거치지 않게 하는 설정용 메서드
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2-console/**");
    }

    /**
     * 시큐리티 설정용 메서드.
     * 1. 인가가 필요한 url의 경우 인증 요구.
     * 2. 기존 테스트 용도로 쓰인 url의 경우 관리자만 사용할 수 있게 적용.
     * 3. 기본 방식인 Form Login의 경우 로그인과 로그아웃 처리
     * 4. OAuth 로그인 방식의 경우의 작업 처리
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/diary/user/**").authenticated()
                .antMatchers("/post/**").authenticated()
                .antMatchers("/update-delete/**").authenticated()
                .antMatchers("/calendar/**").authenticated()
                .antMatchers("/chart-menu/**").authenticated()
                .antMatchers("/profile/**").authenticated()
                /*기존에 테스트 용도로만 쓰는 url 들 접근 막기. 기존 매핑은 관리자만 허락하게 바꿈. */
                .antMatchers("/api/diary/owner/**").hasRole(Role.Admin.name())
                .antMatchers("/api/diary/writer/**").hasRole(Role.Admin.name())
                .antMatchers("/api/diary/diabetes-diary/**").hasRole(Role.Admin.name())
                .antMatchers("/api/diary/diet/**").hasRole(Role.Admin.name())
                .antMatchers("/api/diary/food/**").hasRole(Role.Admin.name())
                .anyRequest().permitAll()
                .and()
                .csrf()
                .ignoringAntMatchers("/h2-console/**")
                .disable()
                .formLogin()/* 로그인이 필요하면 */
                .loginPage("/login-form")/* loginForm 뷰로 이동. */
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")/* 로그인 성공하면 이동하는 디폴트 url 설정. */
                .failureHandler(loginFailHandler())/*로그인 실패 시 처리하는 핸들러 등록. */
                .and()
                .oauth2Login()/*Oauth 로그인 역시 "/loginForm" 으로 이동하게 함.*/
                .loginPage("/login-form")
                .userInfoEndpoint()
                .userService(principalOAuth2UserService);/*Oauth 로그인 이후의 후처리 담당하는 객체. OAuth2UserService 구현체여야 한다.*/

        /*로그아웃 성공 시 인덱스 페이지로 이동.*/
        http.logout().logoutSuccessUrl("/");
    }

    /**
     * 로그인 실패를 처리하는 핸들러를 빈으로 등록하는 메서드
     */
    @Bean
    public LoginFailHandler loginFailHandler() {
        return new LoginFailHandler(judge);
    }
}
