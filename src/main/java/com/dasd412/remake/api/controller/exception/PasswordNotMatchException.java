/*
 * @(#)PasswordNotMatchException.java        1.1.2 2022/3/5
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.exception;

/**
 * id, email을 입력했는데, 매칭되는 기존 비밀번호가 없을 경우 던져지는 예외
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 5일
 */
public class PasswordNotMatchException extends RuntimeException{

    public PasswordNotMatchException(String message) {
        super(message);
    }

    public PasswordNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
