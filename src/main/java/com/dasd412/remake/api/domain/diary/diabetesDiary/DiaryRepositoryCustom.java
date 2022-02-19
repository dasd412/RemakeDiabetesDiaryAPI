/*
 * @(#)DiaryRepositoryCustom.java        1.0.9 2022/2/19
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diabetesDiary;

import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;

/**
 * 혈당일지 리포지토리 상위 인터페이스. Querydsl을 이용하기 위해 구현하였다.
 *
 * @author 양영준
 * @version 1.0.9 2022년 2월 19일
 */
public interface DiaryRepositoryCustom {

    /**
     * @return 식별자의 최댓값. 혈당 일지 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    Long findMaxOfId();

    Optional<Writer> findWriterOfDiary(Long diaryId);

    List<DiabetesDiary> findDiabetesDiariesOfWriter(Long writerId);

    Optional<DiabetesDiary> findOneDiabetesDiaryByIdInWriter(Long writerId, Long diaryId);

    /**
     * @param writerId 작성자 id
     * @param diaryId  일지 id
     * @return 작성자가 작성한 혈당 일지 및 관련된 모든 엔티티
     */
    Optional<DiabetesDiary> findDiabetesDiaryOfWriterWithRelation(Long writerId, Long diaryId);

    /**
     * @param writerId   작성자 id
     * @param predicates where 절 조건문
     * @return 작성자가 작성했던 모든 혈당 일지와 관련된 모든 엔티티 "함께" 조회
     */
    List<DiabetesDiary> findDiabetesDiariesOfWriterWithRelation(Long writerId, List<Predicate> predicates);


    /**
     * @param writerID   작성자 id
     * @param predicates where 조건문
     * @return where 조건문에 맞는 일지들
     */
    List<DiabetesDiary> findDiariesWithWhereClause(Long writerID, List<Predicate> predicates);

    /**
     * 일지와 관련된 엔티티 (식단, 음식) 을 포함하여 "한꺼번에" 제거하는 메서드.
     *
     * @param diaryId 일지 Id
     */
    void bulkDeleteDiary(Long diaryId);

    /**
     * @param writerId   작성자 id
     * @param predicates where 조건
     * @return 전체 기간 공복혈당의 평균 값
     */
    Optional<Double> findAverageFpg(Long writerId, List<Predicate> predicates);

}
