/*
 * @(#)DietRepositoryImpl.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diet;


import com.dasd412.remake.api.domain.diary.BulkDeleteHelper;
import com.dasd412.remake.api.domain.diary.writer.QWriter;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.dasd412.remake.api.domain.diary.diabetesDiary.QDiabetesDiary;

import java.util.List;
import java.util.Optional;

public class DietRepositoryImpl implements DietRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public DietRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * @return 식별자의 최댓값. 식단 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QDiet.diet).select(QDiet.diet.dietId.max()).fetchOne();
    }

    @Override
    public List<Diet> findDietsInDiary(Long writerId, Long diaryId) {

        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.diary.diaryId.eq(diaryId)))
                .fetch();
    }

    @Override
    public Optional<Diet> findOneDietByIdInDiary(Long writerId, Long diaryId, Long dietId) {

        return Optional.ofNullable(jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.diary.diaryId.eq(diaryId)))
                .where(QDiet.diet.dietId.eq(dietId))
                .fetchOne());
    }

    @Override
    public List<Diet> findDietsWithWhereClause(Long writerId, List<Predicate> predicates) {
        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary.writer, QWriter.writer)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(ExpressionUtils.allOf(predicates))
                .fetch();
    }


    @Override
    public Optional<Double> findAverageBloodSugarOfDietWithWhereClause(Long writerId, List<Predicate> predicates) {
        return Optional.ofNullable(jpaQueryFactory.from(QDiet.diet).select(QDiet.diet.bloodSugar.avg())
                .innerJoin(QDiet.diet.diary.writer, QWriter.writer)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(ExpressionUtils.allOf(predicates))
                .fetchOne());
    }

    @Override
    public List<Tuple> findAverageBloodSugarWithWhereClauseGroupByEatTime(Long writerId, List<Predicate> predicates) {
        return jpaQueryFactory
                .select(QDiet.diet.bloodSugar.avg(), QDiet.diet.eatTime)
                .from(QDiet.diet)
                .innerJoin(QDiet.diet.diary.writer, QWriter.writer)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(ExpressionUtils.allOf(predicates))
                .groupBy(QDiet.diet.eatTime)
                .fetch();
    }

    @Override
    public void bulkDeleteDiet(Long dietId) {
        BulkDeleteHelper deleteHelper = new BulkDeleteHelper(jpaQueryFactory);
        deleteHelper.bulkDeleteDiet(dietId);
    }
}
