/*
 * @(#)ConvertLocalDateException.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.exception;

public class ConvertLocalDateException extends RuntimeException{

    public ConvertLocalDateException(String message) {
        super(message);
    }

    public ConvertLocalDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
