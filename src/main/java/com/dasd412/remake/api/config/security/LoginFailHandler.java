/*
 * @(#)LoginFailHandler.java
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

public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuthenticationExceptionJudge judge;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LoginFailHandler(AuthenticationExceptionJudge judge) {
        this.judge = judge;
    }

    /**
     * 로그인 실패 시 브라우저 화면에 에러메시지를 출력해주는 메서드.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException exception) throws IOException, ServletException {
        logger.info("login fail handler");

        String errorMessage = judge.convertErrorMessage(exception);

        logger.info("error : " + errorMessage);

        /* 예외의 인코딩 타입 설정. (한글 깨짐 방지) */
        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");

        setDefaultFailureUrl("/login-form?error=true&exception=" + errorMessage);

        super.onAuthenticationFailure(httpServletRequest, httpServletResponse, exception);
    }


}
