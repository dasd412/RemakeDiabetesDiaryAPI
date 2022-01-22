/*
 * @(#)DuplicateException.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.controller.exception;

/**
 * 중복 이메일 또는 중복 이름 등의 예외 발생 시 사용되는 예외
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
public abstract class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
