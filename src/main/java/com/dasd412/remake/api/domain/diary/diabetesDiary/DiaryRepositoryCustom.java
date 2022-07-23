/*
 * @(#)DiaryRepositoryCustom.java
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

public interface DiaryRepositoryCustom {

    /**
     * @return 식별자의 최댓값. 혈당 일지 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    Long findMaxOfId();

    Optional<Writer> findWriterOfDiary(Long diaryId);

    List<DiabetesDiary> findDiabetesDiariesOfWriter(Long writerId);

    Optional<DiabetesDiary> findOneDiabetesDiaryByIdInWriter(Long writerId, Long diaryId);

    Optional<DiabetesDiary> findDiabetesDiaryWithSubEntitiesOfWriter(Long writerId, Long diaryId);

    List<DiabetesDiary> findDiabetesDiariesWithSubEntitiesOfWriter(Long writerId, List<Predicate> predicates);

    List<DiabetesDiary> findDiariesWithWhereClause(Long writerID, List<Predicate> predicates);

    void bulkDeleteDiary(Long diaryId);

    Optional<Double> findAverageFpg(Long writerId, List<Predicate> predicates);

}
