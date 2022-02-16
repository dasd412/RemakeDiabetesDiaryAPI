/*
 * @(#)DateStringConverter.java        1.0.8 2022/2/16
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.util;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 문자열 <-> LocalDateTime 변환을 도와주는 유틸 클래스.
 *
 * @author 양영준
 * @version 1.0.8 2022년 2월 16일
 */
public class DateStringConverter {

    private DateStringConverter() {
    }

    /**
     * 중복 제거 리팩토링용 도우미 메서드
     *
     * @param allParams RequestParam 해시 맵
     * @return 시작 날짜
     */
    public static LocalDateTime convertMapParamsToStartDate(Map<String, String> allParams) {
        return LocalDateTime.of(Integer.parseInt(allParams.get("startYear")),
                Integer.parseInt(allParams.get("startMonth")),
                Integer.parseInt(allParams.get("startDay")),
                0, 0);
    }

    /**
     * 중복 제거 리팩토링용 도우미 메서드
     *
     * @param allParams RequestParam 해시 맵
     * @return 끝 날짜
     */
    public static LocalDateTime convertMapParamsToEndDate(Map<String, String> allParams) {
        return LocalDateTime.of(Integer.parseInt(allParams.get("endYear")),
                Integer.parseInt(allParams.get("endMonth")),
                Integer.parseInt(allParams.get("endDay")),
                0, 0);
    }

}
