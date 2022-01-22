/*
 * @(#)ApiError.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

/**
 * RestController 리턴 값으로 쓰이는 클래스. 에러가 발생했을 경우 어떤 내용인지 담겨 있다.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

public class ApiError {

    /**
     * 예외 내용
     */
    private final String message;

    /**
     * http 상태 코드
     */
    private final int status;

    ApiError(Throwable throwable, HttpStatus status) {
        this(throwable.getMessage(), status);
    }

    ApiError(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("message", message)
                .append("status", status)
                .toString();
    }
}
