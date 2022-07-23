/*
 * @(#)DateStringConverter.java
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
 */
public class DateStringConverter {

    private DateStringConverter() {
    }

    public static LocalDateTime convertMapParamsToStartDate(Map<String, String> allParams) {
        return LocalDateTime.of(Integer.parseInt(allParams.get("startYear")),
                Integer.parseInt(allParams.get("startMonth")),
                Integer.parseInt(allParams.get("startDay")),
                0, 0);
    }

    public static LocalDateTime convertMapParamsToEndDate(Map<String, String> allParams) {
        return LocalDateTime.of(Integer.parseInt(allParams.get("endYear")),
                Integer.parseInt(allParams.get("endMonth")),
                Integer.parseInt(allParams.get("endDay")),
                0, 0);
    }

    public static boolean isStartDateEqualOrBeforeEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return (startDate != null && endDate != null && (startDate.isEqual(endDate) || startDate.isBefore(endDate)));
    }

    public static LocalDateTime convertLocalDateTime(String year, String month, String day) {
        try {
            return LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 0, 0);
        } catch (NumberFormatException | DateTimeParseException exception) {
            return null;
        }
    }
}
