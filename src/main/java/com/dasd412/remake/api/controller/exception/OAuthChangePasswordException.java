/*
 * @(#)OAuthChangePasswordException.java        1.1.2 2022/3/6
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.exception;

/**
 * OAuth 회원인데, 비밀 번호를 변경하고자 할 경우 던져지는 예외
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 6일
 */
public class OAuthChangePasswordException extends OAuthFindInfoException {
    public OAuthChangePasswordException(String message) {
        super(message);
    }

    public OAuthChangePasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
