/*
 * @(#)PredicateMaker.java        1.0.9 2022/2/18
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
 * Querydsl BooleanBuilder 전담해서 처리하는 객체
 *
 * @author 양영준
 * @version 1.0.9 2022년 2월 18일
 */
public class PredicateMaker {

    private PredicateMaker() {
    }

    /**
     * FoodRepository 출처
     * where 문을 작성할 때, 특히 파라미터의 종류 등에 따라 조건 분기를 하고 싶을 때 Predicate 객체를 사용한다.
     * 작성자 id는 on 절에서 사용되므로 파라미터에서 생략.
     *
     * @param sign       부등호 enum
     * @param bloodSugar 식사 혈당 수치 (만약 식사 혈당에 공백으로 기입되어 있었다면, default 0.
     * @return where 절에 들어가는 조건문 (해당 기간 동안에 식사 혈당과 음식 이름이 함께 작성되었던 일지 중, 부등호 관계와 일치하는 식사 혈당에 기재된 음식들을 리턴할 때 사용한다.)
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

    /**
     * FoodRepository 출처
     * where 문을 작성할 때, 특히 파라미터의 종류 등에 따라 조건 분기를 하고 싶을 때 Predicate 객체를 사용한다.
     *
     * @param startDate 시작 날짜
     * @param endDate   도착 날짜
     * @return where 절에 들어가는 조건문 (해당 기간 사이에 있는가)
     */
    public static Predicate decideBetweenTimeInDiary(LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QDiabetesDiary.diabetesDiary.writtenTime.between(startDate, endDate));
        return booleanBuilder;
    }

    /**
     * DietRepository 출처
     * 식단 -> 일지 참조일 때 사용된다.
     * @param startDate 시작 날짜
     * @param endDate 끝 날짜
     * @return where 절에 들어가는 조건문 (해당 기간 사이에 있는가)
     */
    public static Predicate decideBetweenTimeInDiet(LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QDiet.diet.diary.writtenTime.between(startDate, endDate));
        return booleanBuilder;
    }

    /**
     * FoodRepository 출처
     * inner join diet on 절 이후에 쓰인다.
     *
     * @param sign 부등호 (Equal이면 안된다. double에 대해선 ==을 쓸 수 없기 때문)
     * @return 식단의 평균 혈당 값. 단, join 된 것에 한해서다.
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

    /**
     * DietRepository 출처
     * @param eatTime 식사 시간
     * @return 식사 시간 where 조건문
     */
    public static Predicate decideEatTime(EatTime eatTime) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QDiet.diet.eatTime.eq(eatTime));
        return builder;
    }


}
