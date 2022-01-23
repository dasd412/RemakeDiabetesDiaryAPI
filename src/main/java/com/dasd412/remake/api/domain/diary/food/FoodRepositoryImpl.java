/*
 * @(#)FoodRepositoryImpl.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;

import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Querydsl을 사용하기 위해 만든 구현체 클래스.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
public class FoodRepositoryImpl implements FoodRepositoryCustom {
    /*
    fetch : 조회 대상이 여러건일 경우. 컬렉션 반환
    fetchOne : 조회 대상이 1건일 경우(1건 이상일 경우 에러). generic 에 지정한 타입으로 반환
    fetchFirst : 조회 대상이 1건이든 1건 이상이든 무조건 1건만 반환. 내부에 보면 return limit(1).fetchOne() 으로 되어있음
    fetchCount : 개수 조회. long 타입 반환
    fetchResults : 조회한 리스트 + 전체 개수를 포함한 QueryResults 반환. count 쿼리가 추가로 실행된다.
    */

    /*
    부등호 표현식
    lt <
    loe <=
    gt >
    goe >=
    */

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Querydsl 쿼리 만들 때 사용하는 객체
     */
    private final JPAQueryFactory jpaQueryFactory;

    public FoodRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * @return 식별자의 최댓값. 음식 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QFood.food).select(QFood.food.foodId.max()).fetchOne();
    }

    @Override
    public List<Food> findFoodsInDiet(Long writerId, Long dietId) {
        /* @Query(value = "SELECT food FROM Food as food WHERE food.diet.diary.writer.writerId = :writer_id AND food.diet.diary.diaryId = :diaryId AND food.diet.dietId = :diet_id") */
        return jpaQueryFactory.selectFrom(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.dietId.eq(dietId)))
                .fetch();
    }

    @Override
    public Optional<Food> findOneFoodByIdInDiet(Long writerId, Long dietId, Long foodId) {
        /* @Query(value = "SELECT food FROM Food as food WHERE food.diet.diary.writer.writerId = :writer_id AND food.diet.diary.diaryId = :diary_id AND food.diet.dietId = :diet_id AND food.foodId =:food_id") */
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(QFood.food)
                        .innerJoin(QFood.food.diet, QDiet.diet)
                        .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.dietId.eq(dietId)))
                        .where(QFood.food.foodId.eq(foodId))
                        .fetchOne()
        );
    }

    /**
     * @param writerId   작성자 id
     * @param bloodSugar 식단 혈당
     * @return 식단 혈당 입력보다 높거나 같은 식단들에 기재된 음식들
     */
    @Override
    public List<String> findFoodNamesInDietHigherThanBloodSugar(Long writerId, int bloodSugar) {
        /* @Query(value="SELECT DISTINCT food.foodName FROM Food as food INNER JOIN food.diet diet WHERE diet.bloodSugar >= :blood_sugar AND food.diet.diary.writer.writerId = :writer_id") */
        return jpaQueryFactory.selectDistinct(QFood.food.foodName)
                .from(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(QDiet.diet.bloodSugar.goe(bloodSugar))
                .fetch();
    }

    /**
     * @param writerId 작성자 id
     * @return 작성자의 평균 혈당보다 높거나 같은 식단들에 기재된 음식들
     */
    @Override
    public List<String> findFoodHigherThanAverageBloodSugarOfDiet(Long writerId) {
        /* @Query(value="SELECT DISTINCT food.foodName FROM Food as food INNER JOIN food.diet diet WHERE diet.bloodSugar >= (SELECT AVG(diet.bloodSugar) FROM diet) AND food.diet.diary.writer.writerId = :writer_id") */
        return jpaQueryFactory.selectDistinct(QFood.food.foodName)
                .from(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(QDiet.diet.bloodSugar.goe(
                        JPAExpressions.select(QDiet.diet.bloodSugar.avg())
                                .from(QDiet.diet)
                ))
                .fetch();
    }

    /**
     * 입력 값에 해당하는 음식들 전부 "한꺼번에" 삭제하는 메서드
     *
     * @param foodIds 음식 id 리스트
     */
    @Override
    public void bulkDeleteFood(List<Long> foodIds) {
        jpaQueryFactory.delete(QFood.food)
                .where(QFood.food.foodId.in(foodIds))
                .execute();
    }
}
