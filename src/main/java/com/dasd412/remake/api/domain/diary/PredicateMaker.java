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

    public static Predicate decideEqualitySignOfFastingPlasmaGlucose(InequalitySign sign, int fastingPlasmaGlucose) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        switch (sign) {
            case GREATER:
                booleanBuilder.and(QDiabetesDiary.diabetesDiary.fastingPlasmaGlucose.gt(fastingPlasmaGlucose));
                break;

            case LESSER:
                booleanBuilder.and(QDiabetesDiary.diabetesDiary.fastingPlasmaGlucose.lt(fastingPlasmaGlucose));
                break;

            case EQUAL:
                booleanBuilder.and(QDiabetesDiary.diabetesDiary.fastingPlasmaGlucose.eq(fastingPlasmaGlucose));
                break;

            case GREAT_OR_EQUAL:
                booleanBuilder.and(QDiabetesDiary.diabetesDiary.fastingPlasmaGlucose.goe(fastingPlasmaGlucose));
                break;

            case LESSER_OR_EQUAL:
                booleanBuilder.and(QDiabetesDiary.diabetesDiary.fastingPlasmaGlucose.loe(fastingPlasmaGlucose));
                break;
        }
        return booleanBuilder;
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

    /**
     * inner join diet on 절 이후에 쓰인다.
     * @param sign 부등호 (Equal이면 안된다. double에 대해선 ==을 쓸 수 없기 때문)
     */
    public static Predicate decideAverageOfDiet(InequalitySign sign) {
        checkArgument(sign != InequalitySign.EQUAL && sign != InequalitySign.NONE, "평균을 구할 땐 '=='과 'none' 은 사용할 수 없다.");
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        switch (sign) {
            case GREATER:
                booleanBuilder.and(QDiet.diet.bloodSugar.gt(JPAExpressions.select(QDiet.diet.bloodSugar.avg())
                        .from(QDiet.diet)));
                break;

            case LESSER:
                booleanBuilder.and(QDiet.diet.bloodSugar.lt(JPAExpressions.select(QDiet.diet.bloodSugar.avg())
                        .from(QDiet.diet)));
                break;

            case GREAT_OR_EQUAL:
                booleanBuilder.and(QDiet.diet.bloodSugar.goe(JPAExpressions.select(QDiet.diet.bloodSugar.avg())
                        .from(QDiet.diet)));
                break;

            case LESSER_OR_EQUAL:
                booleanBuilder.and(QDiet.diet.bloodSugar.loe(JPAExpressions.select(QDiet.diet.bloodSugar.avg())
                        .from(QDiet.diet)));
                break;
        }
        return booleanBuilder;
    }

    public static Predicate decideEatTime(EatTime eatTime) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QDiet.diet.eatTime.eq(eatTime));
        return builder;
    }


}
