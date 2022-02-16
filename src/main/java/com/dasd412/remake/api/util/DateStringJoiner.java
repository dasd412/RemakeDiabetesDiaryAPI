/*
 * @(#)DateStringJoiner.java        1.0.8 2022/2/16
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.util;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 년도,월,일,시,분,초에 해당하는 문자열을 LocalDateTime으로 변환하기 위한 객체
 *
 * @author 양영준
 * @version 1.0.8 2022년 2월 16일
 */
@RequiredArgsConstructor
@Builder
public class DateStringJoiner {

    private final String year;
    private final String month;
    private final String day;

    private final String hour;
    private final String minute;
    private final String second;

    public LocalDateTime convertLocalDateTime() {
        String date = this.year + "-" + this.month + "-" + this.day + " " + this.hour + ":" + this.minute + ":" + this.second;
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
