/*
 * @(#)DuplicateUserNameException.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.controller.exception;

/**
 * OAuth 가 아닌 회원 가입 시, username 이  중복되면 발생하는 예외
 */
public class DuplicateUserNameException extends DuplicateException {

    public DuplicateUserNameException(String message) {
        super(message);
    }

    public DuplicateUserNameException(String message, Throwable cause) {
        super(message, cause);
    }

}
