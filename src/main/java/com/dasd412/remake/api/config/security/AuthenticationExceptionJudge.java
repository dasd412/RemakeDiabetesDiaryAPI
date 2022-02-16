/*
 * @(#)LoginFailHandler.java        1.0.8 2022/2/16
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */


package com.dasd412.remake.api.config.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 로그인 실패시 발생한 예외를 분석해서 적절한 예외 메시지를 던져주는 클래스
 *
 * @author 양영준
 * @version 1.0.8 2022년 2월 16일
 */
@Component
public class AuthenticationExceptionJudge {

    public String convertErrorMessage(AuthenticationException e) {
        String errorMessage;
        if (e instanceof BadCredentialsException || e instanceof InternalAuthenticationServiceException) {
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다.";
        } else if (e instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 아이디 입니다.";
        } else {
            errorMessage = "알 수 없는 이유로 로그인이 안되고 있습니다.";
        }
        return errorMessage;
    }

}
