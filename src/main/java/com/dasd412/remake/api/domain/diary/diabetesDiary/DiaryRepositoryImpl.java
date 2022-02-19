/*
 * @(#)DiaryRepositoryImpl.java        1.0.9 2022/2/19
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

/**
 * Querydsl을 사용하기 위해 만든 구현체 클래스.
 *
 * @author 양영준
 * @version 1.0.9 2022년 2월 19일
 */
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

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
        /* @Query(value = "SELECT diary.writer FROM DiabetesDiary diary WHERE diary.diaryId = :diary_id") */
        return Optional.ofNullable(jpaQueryFactory.from(QDiabetesDiary.diabetesDiary).select(QDiabetesDiary.diabetesDiary.writer)
                .where(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId))
                .fetchOne());
    }

    @Override
    public List<DiabetesDiary> findDiabetesDiariesOfWriter(Long writerId) {
        /* @Query(value = "FROM DiabetesDiary diary WHERE diary.writer.writerId = :writer_id") */
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId))
                .fetch();
    }

    @Override
    public Optional<DiabetesDiary> findOneDiabetesDiaryByIdInWriter(Long writerId, Long diaryId) {
        /* @Query(value = "FROM DiabetesDiary diary WHERE diary.writer.writerId = :writer_id AND diary.diaryId = :diary_id") */
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId).and(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId)))
                .fetchOne());
    }

    /**
     * fetch join 활용해서 연관된 엔티티 모두 조회하는 메서드. (n+1 문제 없음.)
     *
     * @param writerId 작성자 id
     * @param diaryId  일지 id
     * @return 작성자가 작성한 혈당 일지 및 관련된 모든 엔티티
     */
    @Override
    public Optional<DiabetesDiary> findDiabetesDiaryOfWriterWithRelation(Long writerId, Long diaryId) {
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
     *
     * @param writerId 작성자 id
     * @return 작성자가 작성했던 모든 혈당 일지와 관련된 모든 엔티티 "함께" 조회
     */
    @Override
    public List<DiabetesDiary> findDiabetesDiariesOfWriterWithRelation(Long writerId, List<Predicate> predicates) {
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

    /**
     * @param writerID   작성자 id
     * @param predicates where 조건문
     * @return where 조건문에 맞는 일지들
     */
    @Override
    public List<DiabetesDiary> findDiariesWithWhereClause(Long writerID, List<Predicate> predicates) {
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .on(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerID))
                .where(ExpressionUtils.allOf(predicates))
                .fetch();
    }

    /**
     * 일지와 관련된 엔티티 (식단, 음식) 을 포함하여 "한꺼번에" 제거하는 메서드.
     *
     * @param diaryId 일지 Id
     */
    @Override
    public void bulkDeleteDiary(Long diaryId) {
        BulkDeleteHelper deleteHelper = new BulkDeleteHelper(jpaQueryFactory);
        deleteHelper.bulkDeleteDiary(diaryId);
    }

    /**
     * @param writerId   작성자 id
     * @param predicates where 조건
     * @return 평균 공복 혈당
     */
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
