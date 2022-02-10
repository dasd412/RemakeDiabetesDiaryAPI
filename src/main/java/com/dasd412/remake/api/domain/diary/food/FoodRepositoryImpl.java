/*
 * @(#)FoodRepositoryImpl.java        1.0.7 2022/2/10
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;

import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.dasd412.remake.api.domain.diary.InequalitySign;
import com.dasd412.remake.api.domain.diary.diabetesDiary.QDiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Querydsl을 사용하기 위해 만든 구현체 클래스.
 *
 * @author 양영준
 * @version 1.0.7 2022년 2월 10일
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

    /**
     * decideEqualitySign(),decideBetween() 과 연계해서 쓰면 된다.
     * 리턴 값이 추후에 연관관계 조회할 일이 없기 때문에 fetch join 안함.
     * <p>
     * (유의점)
     * 단순히 Page를 반환하는 쿼리를 작성할 경우에는 count 쿼리가 추가적으로 실행된다고 한다.
     * 이는 쓸데없는 쿼리를 한 번 더 날리는 비효율을 초래하기 때문에 최적화를 할 필요가 있다.
     *
     * @param writerId   작성자 id
     * @param predicates where 절에 쓰이는 조건문 객체들
     * @param pageable   페이징 객체
     * @return 해당 기간 동안 작성자가 작성한 음식에 관한 정보들
     */
    @Override
    public Page<FoodBoardDTO> findFoodsWithPagination(Long writerId, List<Predicate> predicates, Pageable pageable) {

        /*
        List의 경우 추가 count 없이 결과만 반환한다.
        */
        List<Tuple> foodList = jpaQueryFactory.select(QFood.food.foodName, QDiet.diet.bloodSugar, QDiabetesDiary.diabetesDiary.writtenTime)
                .from(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .innerJoin(QFood.food.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(ExpressionUtils.allOf(predicates)) /* where 절에 쓰이는 조건문은 "가변적" */
                .orderBy(QDiet.diet.bloodSugar.desc(), QDiabetesDiary.diabetesDiary.writtenTime.desc(), QFood.food.foodName.asc())
                .offset(pageable.getOffset()) /* offset = page * size */
                .limit(pageable.getPageSize())
                .fetch();

        List<FoodBoardDTO> dtoList = foodList
                .stream()
                .map(tuple -> new FoodBoardDTO(tuple.get(QFood.food.foodName), tuple.get(QDiet.diet.bloodSugar), tuple.get(QDiabetesDiary.diabetesDiary.writtenTime)))
                .collect(Collectors.toList());

        /*
        count 쿼리를 분리하여 최적화 한다.
         */

        JPAQuery<Food> countFood = jpaQueryFactory
                .select(QFood.food)
                .from(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .innerJoin(QFood.food.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(ExpressionUtils.allOf(predicates));

        return PageableExecutionUtils.getPage(dtoList, pageable, countFood::fetchCount);
    }

    /**
     * where 문을 작성할 때, 특히 파라미터의 종류 등에 따라 조건 분기를 하고 싶을 때 Predicate 객체를 사용한다.
     * 작성자 id는 on 절에서 사용되므로 파라미터에서 생략.
     *
     * @param sign       부등호 enum
     * @param bloodSugar 식사 혈당 수치 (만약 식사 혈당에 공백으로 기입되어 있었다면, default 0.
     * @return where 절에 들어가는 조건문 (해당 기간 동안에 식사 혈당과 음식 이름이 함께 작성되었던 일지 중, 부등호 관계와 일치하는 식사 혈당에 기재된 음식들을 리턴할 때 사용한다.)
     */
    @Override
    public Predicate decideEqualitySign(InequalitySign sign, int bloodSugar) {
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
     * where 문을 작성할 때, 특히 파라미터의 종류 등에 따라 조건 분기를 하고 싶을 때 Predicate 객체를 사용한다.
     *
     * @param startDate 시작 날짜
     * @param endDate   도착 날짜
     * @return where 절에 들어가는 조건문 (해당 기간 사이에 있는가)
     */
    @Override
    public Predicate decideBetween(LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QDiabetesDiary.diabetesDiary.writtenTime.between(startDate, endDate));
        return booleanBuilder;
    }
}
