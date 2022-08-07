/*
 * @(#)PredicateMaker.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary;

import com.dasd412.remake.api.domain.diary.diabetesDiary.QDiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Querydsl BooleanBuilder 전담해서 처리
 */
public class PredicateMaker {

    private PredicateMaker() {
    }
    /**
     * 작성자 id는 on 절에서 사용되므로 파라미터에서 생략.
     */
    public static Predicate decideEqualitySignOfBloodSugar(InequalitySign sign, int bloodSugar) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        switch (sign) {
            case GREATER:
                booleanBuilder.and(QDiet.diet.bloodSugar.gt(bloodSugar));
                break;

            case LESSER:
                booleanBuilder.and(QDiet.diet.bloodSugar.lt(bloodSugar));
                break;

            case EQUAL:
                booleanBuilder.and(QDiet.diet.bloodSugar.eq(bloodSugar));
                break;

            case GREAT_OR_EQUAL:
                booleanBuilder.and(QDiet.diet.bloodSugar.goe(bloodSugar));
                break;

            case LESSER_OR_EQUAL:
                booleanBuilder.and(QDiet.diet.bloodSugar.loe(bloodSugar));
                break;
        }

        return booleanBuilder;
    }

    public static Predicate decideBetweenTimeInDiary(LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QDiabetesDiary.diabetesDiary.writtenTime.between(startDate, endDate));
        return booleanBuilder;
    }

    public static Predicate decideBetweenTimeInDiet(LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QDiet.diet.diary.writtenTime.between(startDate, endDate));
        return booleanBuilder;
    }
}
