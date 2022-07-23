/*
 * @(#)FoodPageVO.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_view;

import com.dasd412.remake.api.controller.exception.ConvertLocalDateException;
import com.dasd412.remake.api.domain.diary.InequalitySign;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static com.dasd412.remake.api.util.DateStringConverter.convertLocalDateTime;
import static com.google.common.base.Preconditions.checkArgument;

@Getter
public class FoodPageVO {

    private static final int DEFAULT_SIZE = 10;
    private static final int DEFAULT_MAX_SIZE = 50;

    /**
     * 페이지 번호 (브라우저에서 전달되는 값은 1이다.)
     * 그런데 Pageble 객체의 offset = page * size이기 때문에 pageable 객체를 만들 때는 page-1 해준다.
     */
    private int page;

    private int size;

    private int bloodSugar;

    private String sign;

    private String startYear;

    private String startMonth;

    private String startDay;

    private String endYear;

    private String endMonth;

    private String endDay;

    public FoodPageVO() {
        this.page = 1;
        this.size = DEFAULT_SIZE;

        this.sign = "";
        this.bloodSugar = 0;

        this.startYear = "";
        this.startMonth = "";
        this.startDay = "";

        this.endYear = "";
        this.endMonth = "";
        this.endDay = "";
    }

    public void setPage(int page) {
        this.page = page < 0 ? 1 : page;
    }

    /**
     * 만약, 음식 이름 게시물의 수가 기본 10개보다 적거나, 50개를 초과할 경우에는 기본 사이즈인 10으로 정해준다.
     *
     * @param size 음식 이름 게시물의 수
     */
    public void setSize(int size) {
        this.size = size < DEFAULT_SIZE || size > DEFAULT_MAX_SIZE ? DEFAULT_SIZE : size;
    }

    /**
     * Pageble 객체의 offset = page * size이기 때문에 pageable 객체를 만들 때는 page-1 해준다.
     *
     * @param direction  정렬 방향
     * @param properties 정렬 기준
     */
    public Pageable makePageable(Sort.Direction direction, String... properties) {
        return PageRequest.of(this.page - 1, this.size, direction, properties);
    }

    /**
     * 이 메서드를 사용하면, 리포지토리 코드 내에서 orderBy()를 지정해줘야 한다.
     *
     * @return 정렬 방향 및 기준 없이 디폴트로 된 페이징 설정 객체
     */
    public Pageable makePageable() {
        return PageRequest.of(this.page - 1, this.size);
    }

    public void setBloodSugar(int bloodSugar) {
        checkArgument(bloodSugar >= 0, "blood sugar must be zero or positive");
        this.bloodSugar = bloodSugar;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public InequalitySign getEnumOfSign() {
        switch (this.sign) {
            case "lesser":
                return InequalitySign.LESSER;

            case "greater":
                return InequalitySign.GREATER;

            case "equal":
                return InequalitySign.EQUAL;

            case "le":
                return InequalitySign.LESSER_OR_EQUAL;

            case "ge":
                return InequalitySign.GREAT_OR_EQUAL;

            default:
                return InequalitySign.NONE;
        }
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public LocalDateTime convertStartDate() {
        return convertLocalDateTime(this.startYear, this.startMonth, this.startDay).orElseThrow(() -> new ConvertLocalDateException("시작 날짜 변환 실패"));
    }

    public LocalDateTime convertEndDate() {
        return convertLocalDateTime(this.endYear, this.endMonth, this.endDay).orElseThrow(() -> new ConvertLocalDateException("끝 날짜 변환 실패"));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("page", page)
                .append("size", size)
                .append("blood_sugar", bloodSugar)
                .append("sign", sign)
                .append("startDate", convertStartDate())
                .append("endDate", convertEndDate())
                .toString();
    }
}
