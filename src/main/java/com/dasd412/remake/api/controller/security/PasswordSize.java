/*
 * @(#)PasswordSize.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security;

/**
 * dto 에서 비밀 번호 크기 지정할 때 쓰이는 상수 지정용 인터페이스.
 */
public interface PasswordSize {

    int MIN_SIZE = 8;

    int MAX_SIZE = 20;

}
