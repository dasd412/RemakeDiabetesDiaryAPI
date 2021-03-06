/*
 * @(#)GlobalExceptionHandler.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.persistence.NoResultException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class,
            TypeMismatchException.class, MissingServletRequestParameterException.class,
            JSONException.class, MethodArgumentNotValidException.class})
    public ModelAndView handle400(Throwable throwable) {
        logger.error("Bad Request: {}", throwable.getMessage());
        return new ModelAndView("400");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handle403() {
        logger.info("not authorized");
        return new ModelAndView("403");
    }

    /**
     * 참고로 NoResultException 은 쿼리 결과가 하나도 없을 때 발생하는 (jpa 가 제공하는) 예외이다.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class, NoResultException.class})
    public ModelAndView handle404() {
        logger.error("Not found in server");
        return new ModelAndView("404");
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handle405(HttpRequestMethodNotSupportedException throwable) {
        logger.warn("Method not allowed : {}", throwable.getMethod());
        return new ModelAndView("405");
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeException.class)
    public ModelAndView handle415(HttpMediaTypeException throwable) {
        logger.warn("Unsupported Media Type : {}", throwable.getMessage());
        return new ModelAndView("415");
    }

    /**
     * @param throwable 다른 예외 캐치에서 잡히지 못한 예외들
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handle500(Throwable throwable) {
        logger.error("Internal server error : {}", throwable.getMessage());
        return new ModelAndView("500");
    }
}
