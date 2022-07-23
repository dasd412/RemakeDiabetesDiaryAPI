/*
 * @(#)PasswordConfirmException.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.exception;

/**
 * 비밀 번호 변경 시도 중, 비밀 번호랑 비밀 번호 확인이 다를 경우 던져지는 예외.
 *
 */
public class PasswordConfirmException extends RuntimeException {

    public PasswordConfirmException(String message) {
        super(message);
    }

    public PasswordConfirmException(String message, Throwable cause) {
        super(message, cause);
    }
}

