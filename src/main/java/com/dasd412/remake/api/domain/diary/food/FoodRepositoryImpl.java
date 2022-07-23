/*
 * @(#)FoodRepositoryImpl.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;

import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.dasd412.remake.api.domain.diary.diabetesDiary.QDiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class FoodRepositoryImpl implements FoodRepositoryCustom {

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

        return jpaQueryFactory.selectFrom(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.dietId.eq(dietId)))
                .fetch();
    }

    @Override
    public Optional<Food> findOneFoodByIdInDiet(Long writerId, Long dietId, Long foodId) {

        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(QFood.food)
                        .innerJoin(QFood.food.diet, QDiet.diet)
                        .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.dietId.eq(dietId)))
                        .where(QFood.food.foodId.eq(foodId))
                        .fetchOne()
        );
    }

    @Override
    public List<String> findFoodNamesInDietWithWhereClause(Long writerId, List<Predicate> predicates) {
        return jpaQueryFactory.selectDistinct(QFood.food.foodName)
                .from(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(ExpressionUtils.allOf(predicates))
                .fetch();
    }

    @Override
    public void bulkDeleteFood(List<Long> foodIds) {
        jpaQueryFactory.delete(QFood.food)
                .where(QFood.food.foodId.in(foodIds))
                .execute();
    }

    /**
     * 리턴 값이 추후에 연관관계 조회할 일이 없기 때문에 fetch join 안함.
     * (유의점)
     * 단순히 Page를 반환하는 쿼리를 작성할 경우에는 count 쿼리가 추가적으로 실행된다고 한다.
     * 이는 쓸데없는 쿼리를 한 번 더 날리는 비효율을 초래하기 때문에 최적화를 할 필요가 있다.
     */
    @Override
    public Page<FoodBoardDTO> findFoodsWithPaginationAndWhereClause(Long writerId, List<Predicate> predicates, Pageable pageable) {

        /*
        List의 경우 추가 count 없이 결과만 반환한다.
        */
        List<Tuple> foodList = jpaQueryFactory.select(QFood.food.foodName, QDiet.diet.bloodSugar, QDiabetesDiary.diabetesDiary.writtenTime, QDiabetesDiary.diabetesDiary.diaryId)
                .from(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .innerJoin(QFood.food.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(ExpressionUtils.allOf(predicates))
                .orderBy(QDiet.diet.bloodSugar.desc(), QDiabetesDiary.diabetesDiary.writtenTime.desc(), QFood.food.foodName.asc())
                .offset(pageable.getOffset()) /* offset = page * size */
                .limit(pageable.getPageSize())
                .fetch();

        List<FoodBoardDTO> dtoList = foodList
                .stream()
                .map(tuple -> new FoodBoardDTO(tuple.get(QFood.food.foodName), tuple.get(QDiet.diet.bloodSugar), tuple.get(QDiabetesDiary.diabetesDiary.writtenTime), tuple.get(QDiabetesDiary.diabetesDiary.diaryId)))
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
}
