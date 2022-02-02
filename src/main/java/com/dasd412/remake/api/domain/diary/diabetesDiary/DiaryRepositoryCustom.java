/*
 * @(#)DiaryRepositoryCustom.java        1.0.4 2022/2/2
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diabetesDiary;

import com.dasd412.remake.api.domain.diary.writer.Writer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 혈당일지 리포지토리 상위 인터페이스. Querydsl을 이용하기 위해 구현하였다.
 *
 * @author 양영준
 * @version 1.0.4 2022년 2월 2일
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
     * @param writerId 작성자 id
     * @return 작성자가 작성했던 모든 혈당 일지와 관련된 모든 엔티티 "함께" 조회
     */
    List<DiabetesDiary> findDiabetesDiariesOfWriterWithRelation(Long writerId);

    /**
     * @param writerId  작성자 id
     * @param startDate 시작 날짜
     * @param endDate   끝 날짜
     * @return 해당 기간 동안 작성자가 작성했던 모든 혈당 일지와 관련된 모든 엔티티 "함께" 조회
     */
    List<DiabetesDiary> findDiariesWithRelationBetweenTime(Long writerId, LocalDateTime startDate, LocalDateTime endDate);

    List<DiabetesDiary> findDiaryBetweenTime(Long writerId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * @param writerId             작성자 id
     * @param fastingPlasmaGlucose 공복 혈당
     * @return 입력 공복 혈당보다 높거나 같게 기재된 혈당 일지들
     */
    List<DiabetesDiary> findFpgHigherOrEqual(Long writerId, int fastingPlasmaGlucose);

    /**
     * @param writerId             작성자 id
     * @param fastingPlasmaGlucose 공복 혈당
     * @return 입력 공복 혈당보다 낮거나 같게 기재된 혈당 일지들
     */
    List<DiabetesDiary> findFpgLowerOrEqual(Long writerId, int fastingPlasmaGlucose);

    /**
     * 일지와 관련된 엔티티 (식단, 음식) 을 포함하여 "한꺼번에" 제거하는 메서드.
     *
     * @param diaryId 일지 Id
     */
    void bulkDeleteDiary(Long diaryId);

    /**
     * @param writerId 작성자 id
     * @return 공복혈당의 평균 값
     */
    Optional<Double> findAverageFpg(Long writerId);

}
