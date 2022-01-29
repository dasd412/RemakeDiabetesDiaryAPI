/*
 * @(#)DietRepositoryCustom.java        1.0.3 2022/1/29
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 식단 리포지토리 상위 인터페이스. Querydsl을 이용하기 위해 구현하였다.
 *
 * @author 양영준
 * @version 1.0.3 2022년 1월 29일
 */
public interface DietRepositoryCustom {

    /**
     * @return 식별자의 최댓값. 식단 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    Long findMaxOfId();

    List<Diet> findDietsInDiary(Long writerId, Long diaryId);

    Optional<Diet> findOneDietByIdInDiary(Long writerId, Long diaryId, Long DietId);

    /**
     * 
     * @param writerId 작성자 id
     * @return 작성자가 갖고 있는 모든 식단 정보
     */
    List<Diet>findAllBloodSugar(Long writerId);
    
    /**
     *
     * @param writerId 작성자 id
     * @param startDate 시작 날짜
     * @param endDate 끝 날짜
     * @return 기간 내 모든 식사 혈당
     */
    List<Diet>findAllBloodSugarBetweenTime(Long writerId,LocalDateTime startDate, LocalDateTime endDate);

    List<Diet> findHigherThanBloodSugarBetweenTime(Long writerId, int bloodSugar, LocalDateTime startDate, LocalDateTime endDate);

    List<Diet> findLowerThanBloodSugarBetweenTime(Long writerId, int bloodSugar, LocalDateTime startDate, LocalDateTime endDate);

    List<Diet> findHigherThanBloodSugarInEatTime(Long writerId, int bloodSugar, EatTime eatTime);

    List<Diet> findLowerThanBloodSugarInEatTime(Long writerId, int bloodSugar, EatTime eatTime);

    Optional<Double> findAverageBloodSugarOfDiet(Long writerId);

    /**
     * 식단과 관련된 엔티티 (음식) 을 포함하여 "한꺼번에" 제거하는 메서드.
     *
     * @param dietId 식단 id
     */
    void bulkDeleteDiet(Long dietId);
}
