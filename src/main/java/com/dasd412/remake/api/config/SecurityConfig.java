/*
 * @(#)SecurityConfig.java
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

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOAuth2UserService principalOAuth2UserService;

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
                .anyRequest().permitAll()
                .and()
                .csrf()
                .ignoringAntMatchers("/h2-console/**")
                .disable()
                .formLogin()/* 로그인이 필요하면 */
                .loginPage("/login-form")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .failureHandler(loginFailHandler())
                .and()
                .oauth2Login()
                .loginPage("/login-form")
                .userInfoEndpoint()
                .userService(principalOAuth2UserService);

        http.logout().logoutSuccessUrl("/");
    }

    @Bean
    public LoginFailHandler loginFailHandler() {
        return new LoginFailHandler(judge);
    }
}
