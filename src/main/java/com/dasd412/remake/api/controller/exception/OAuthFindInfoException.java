/*
 * @(#)OAuthFindInfoException.java        1.1.2 2022/3/2
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.exception;

/**
 * OAuth 회원인데, id, 비밀번호 등 정보를 찾고자 할 경우 던져지는 예외
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 2일
 */
public abstract class OAuthFindInfoException extends RuntimeException {

    public OAuthFindInfoException(String message) {
        super(message);
    }

    public OAuthFindInfoException(String message, Throwable cause) {
        super(message, cause);
    }

}
