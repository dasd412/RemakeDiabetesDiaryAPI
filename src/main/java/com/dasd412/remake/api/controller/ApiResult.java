/*
 * @(#)ApiResult.java        1.0.1 2022/1/22
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
 * RestController의 리턴 값으로 쓰인다. 메시지의 공통 표준을 주기 위해서 만들었다.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

public class ApiResult<T> {

    /**
     * RestController 메서드가 성공했는지 여부를 나타낸다.
     */
    private final boolean success;

    /**
     * 이 클래스가 감쌀 객체.
     */
    private final T response;

    /**
     * 에러가 발생했을 경우 담을 객체.
     */
    private final ApiError error;

    private ApiResult(boolean success, T response, ApiError error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }

    /**
     * RestController 메서드 성공시 사용된다.
     *
     * @param response 이 클래스가 감쌀 객체
     */
    public static <T> ApiResult<T> OK(T response) {
        return new ApiResult<>(true, response, null);
    }

    /**
     * RestController 메서드 실패 시 예외 내용을 알려주기 위해 사용된다
     *
     * @param throwable 예외 객체
     * @param status    http 상태 값
     */
    public static ApiResult<?> ERROR(Throwable throwable, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(throwable, status));
    }

    public static ApiResult<?> ERROR(String errorMessage, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(errorMessage, status));
    }

    public boolean isSuccess() {
        return success;
    }

    public T getResponse() {
        return response;
    }

    public ApiError getError() {
        return error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("success", success)
                .append("response", response)
                .append("error", error)
                .toString();
    }
}
