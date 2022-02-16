/*
 * @(#)LoginFailHandler.java        1.0.8 2022/2/16
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 로그인을 실패하면 처리해주는 핸들러 클래스.
 * AuthenticationFailureHandler 의 구현체를  WebSecurityConfigurerAdapter 상속체에 핸들러로 등록하면 로그인 실패 시 예외를 핸들링해 줄 수 있다.
 * 그리고 SimpleUrlAuthenticationFailureHandler 는 AuthenticationFailureHandler 구현체다.
 *
 * @author 양영준
 * @version 1.0.8 2022년 2월 16일
 */

public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuthenticationExceptionJudge judge;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LoginFailHandler(AuthenticationExceptionJudge judge) {
        this.judge = judge;
    }

    /**
     * 로그인 실패 시 브라우저 화면에 에러메시지를 출력해주는 메서드.
     *
     * @param httpServletRequest  Request에 대한 정보를 담고 있다.
     * @param httpServletResponse Resonse에 대해 설정할 수 있다.
     * @param exception                   로그인 실패 시 예외에 대한 정보를 담고 있다.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException exception) throws IOException, ServletException {
        logger.info("login fail handler");

        /* 예외의 타입을 확인한다. */
        String errorMessage = judge.convertErrorMessage(exception);

        logger.info("error : " + errorMessage);

        /* 예외의 인코딩 타입 설정. (한글 깨짐 방지) */
        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");

        /* 로그인 실패 시 이동하는 URL 설정*/
        setDefaultFailureUrl("/login-form?error=true&exception=" + errorMessage);

        super.onAuthenticationFailure(httpServletRequest, httpServletResponse, exception);
    }


}
