/*
 * @(#)DietRepositoryCustom.java
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

public interface DietRepositoryCustom {

    /**
     * @return 식별자의 최댓값. 식단 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    Long findMaxOfId();

    List<Diet> findDietsInDiary(Long writerId, Long diaryId);

    Optional<Diet> findOneDietByIdInDiary(Long writerId, Long diaryId, Long DietId);

    List<Diet> findDietsWithWhereClause(Long writerId, List<Predicate> predicates);

    Optional<Double> findAverageBloodSugarOfDietWithWhereClause(Long writerId, List<Predicate> predicates);

    List<Tuple> findAverageBloodSugarWithWhereClauseGroupByEatTime(Long writerId, List<Predicate> predicates);

    void bulkDeleteDiet(Long dietId);
}
