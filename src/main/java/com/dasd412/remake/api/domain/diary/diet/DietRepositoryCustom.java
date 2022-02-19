/*
 * @(#)DietRepositoryCustom.java        1.0.9 2022/2/19
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diet;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 식단 리포지토리 상위 인터페이스. Querydsl을 이용하기 위해 구현하였다.
 *
 * @author 양영준
 * @version 1.0.9 2022년 2월 19일
 */
public interface DietRepositoryCustom {

    /**
     * @return 식별자의 최댓값. 식단 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    Long findMaxOfId();

    List<Diet> findDietsInDiary(Long writerId, Long diaryId);

    Optional<Diet> findOneDietByIdInDiary(Long writerId, Long diaryId, Long DietId);

    /**
     * @param writerId   작성자 id
     * @param predicates where 절 조건문
     * @return where 절 조건에 해당하는 식단 내용 찾기
     */
    List<Diet> findDietsWithWhereClause(Long writerId, List<Predicate> predicates);

    /**
     * @param writerId   작성자 id
     * @param predicates where 절 조건문
     * @return 조건에 일치하는 평균 식사 혈당
     */
    Optional<Double> findAverageBloodSugarOfDiet(Long writerId, List<Predicate> predicates);


    /**
     * @param writerId   작성자 id
     * @param predicates where절 조건문
     * @return 조건에 해당하는 식사 시간 별(평균 혈당, 식사 시간) 형태의 튜플들
     */
    List<Tuple> findAverageBloodSugarGroupByEatTime(Long writerId, List<Predicate> predicates);

    /**
     * 식단과 관련된 엔티티 (음식) 을 포함하여 "한꺼번에" 제거하는 메서드.
     *
     * @param dietId 식단 id
     */
    void bulkDeleteDiet(Long dietId);
}
