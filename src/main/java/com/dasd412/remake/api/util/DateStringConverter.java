/*
 * @(#)DateStringConverter.java        1.0.8 2022/2/16
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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

    /**
     * @param startDate 시작 날짜
     * @param endDate   끝 날짜
     * @return 시작 날짜가 끝 날짜와 같거나 더 앞선지 여부
     */
    public static boolean isStartDateEqualOrBeforeEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return (startDate != null && endDate != null && (startDate.isEqual(endDate) || startDate.isBefore(endDate)));
    }

    /**
     * @param year 연도
     * @param month 월
     * @param day 일
     * @return 변환의 오류가 없다면 LocalDateTime 객체 리턴. 만약 오류가 있다면 null 리턴.
     */
    public static LocalDateTime convertLocalDateTime(String year, String month, String day) {
        try {
            return LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 0, 0);
        } catch (NumberFormatException | DateTimeParseException exception) {
            return null;
        }
    }
}
