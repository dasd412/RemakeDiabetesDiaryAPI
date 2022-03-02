/*
 * @(#)RegexChecker.java        1.1.2 2022/3/2
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.util;

import java.util.regex.Pattern;

/**
 * 이메일 등 정규식 체킹용 유틸리티.
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 2일
 */
public class RegexChecker {

    public static String EMAIL_PATTERN = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";

    public static boolean isRightEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }
}
