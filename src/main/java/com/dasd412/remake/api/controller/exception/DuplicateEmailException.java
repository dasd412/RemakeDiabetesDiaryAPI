/*
 * @(#)DuplicateEmailException.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.controller.exception;

/**
 * OAuth 가 아닌 회원 가입 시, 이메일이 중복되면 사용되는 예외 클래스
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
public class DuplicateEmailException extends DuplicateException {

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
