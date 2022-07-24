/*
 * @(#)DiaryRepositoryImpl.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diabetesDiary;

import com.dasd412.remake.api.domain.diary.BulkDeleteHelper;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.dasd412.remake.api.domain.diary.food.QFood;
import com.dasd412.remake.api.domain.diary.writer.QWriter;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.dasd412.remake.api.domain.diary.writer.Writer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiaryRepositoryImpl implements DiaryRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public DiaryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * @return 식별자의 최댓값. 혈당 일지 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QDiabetesDiary.diabetesDiary).select(QDiabetesDiary.diabetesDiary.diaryId.max())
                .fetchOne();
    }

    @Override
    public Optional<Writer> findWriterOfDiary(Long diaryId) {
        return Optional.ofNullable(jpaQueryFactory.from(QDiabetesDiary.diabetesDiary).select(QDiabetesDiary.diabetesDiary.writer)
                .where(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId))
                .fetchOne());
    }

    @Override
    public List<DiabetesDiary> findDiabetesDiariesOfWriter(Long writerId) {
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId))
                .fetch();
    }

    @Override
    public Optional<DiabetesDiary> findOneDiabetesDiaryByIdInWriter(Long writerId, Long diaryId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId).and(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId)))
                .fetchOne());
    }

    /**
     * fetch join 활용해서 연관된 엔티티 모두 조회하는 메서드. (n+1 문제 없음.)
     */
    @Override
    public Optional<DiabetesDiary> findDiabetesDiaryWithSubEntitiesOfWriter(Long writerId, Long diaryId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .fetchJoin()
                .leftJoin(QDiabetesDiary.diabetesDiary.dietList, QDiet.diet)
                .fetchJoin()
                .leftJoin(QDiet.diet.foodList, QFood.food)
                .fetchJoin()
                .where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId).and(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId)))
                .fetchOne());
    }

    /**
     * 단순히 fetch()를 하게 되면, 조인된 테이블의 개수만큼 중복된 엔티티를 얻어오게 된다.
     * 따라서 stream().distinct().collect(Collectors.toList()) 를 이용하여 java 단에서 중복을 제거해준다.
     */
    @Override
    public List<DiabetesDiary> findDiabetesDiariesWithSubEntitiesOfWriter(Long writerId, List<Predicate> predicates) {
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .fetchJoin()
                .leftJoin(QDiabetesDiary.diabetesDiary.dietList, QDiet.diet)
                .fetchJoin()
                .leftJoin(QDiet.diet.foodList, QFood.food)
                .fetchJoin()
                .where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId)
                        .and(ExpressionUtils.allOf(predicates)))
                .fetch().stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<DiabetesDiary> findDiariesWithWhereClause(Long writerID, List<Predicate> predicates) {
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .on(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerID))
                .where(ExpressionUtils.allOf(predicates))
                .fetch();
    }

    @Override
    public void bulkDeleteDiary(Long diaryId) {
        BulkDeleteHelper deleteHelper = new BulkDeleteHelper(jpaQueryFactory);
        deleteHelper.bulkDeleteDiary(diaryId);
    }

    @Override
    public Optional<Double> findAverageFpg(Long writerId, List<Predicate> predicates) {
        return Optional.ofNullable(jpaQueryFactory.from(QDiabetesDiary.diabetesDiary)
                .select(QDiabetesDiary.diabetesDiary.fastingPlasmaGlucose.avg())
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .on(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId))
                .where(ExpressionUtils.allOf(predicates))
                .fetchOne());
    }

}
