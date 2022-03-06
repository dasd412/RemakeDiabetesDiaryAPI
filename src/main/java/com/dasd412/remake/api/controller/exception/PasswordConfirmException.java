/*
 * @(#)PasswordConfirmException.java        1.1.2 2022/3/6
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.exception;

/**
 * 비밀 번호 변경 시도 중, 비밀 번호랑 비밀 번호 확인이 다를 경우 던져지는 예외.
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 6일
 */
public class PasswordConfirmException extends RuntimeException {

    public PasswordConfirmException(String message) {
        super(message);
    }

    public PasswordConfirmException(String message, Throwable cause) {
        super(message, cause);
    }
}

