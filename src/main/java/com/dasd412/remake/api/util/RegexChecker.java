/*
 * @(#)RegexChecker.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.util;

import java.util.regex.Pattern;

public class RegexChecker {

    public static final String EMAIL_PATTERN = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";

    public static boolean isRightEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }
}
