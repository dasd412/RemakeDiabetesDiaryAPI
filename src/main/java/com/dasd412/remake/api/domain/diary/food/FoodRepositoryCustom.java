/*
 * @(#)FoodRepositoryCustom.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;

import java.util.List;
import java.util.Optional;

/**
 * 음식 리포지토리 상위 인터페이스. Querydsl을 이용하기 위해 구현하였다.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
public interface FoodRepositoryCustom {

    /**
     * @return 식별자의 최댓값. 음식 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    Long findMaxOfId();

    List<Food> findFoodsInDiet(Long writerId, Long dietId);

    Optional<Food> findOneFoodByIdInDiet(Long writerId, Long dietId, Long foodId);

    /**
     * @param writerId   작성자 id
     * @param bloodSugar 식단 혈당
     * @return 식단 혈당 입력보다 높거나 같은 식단들에 기재된 음식들
     */
    List<String> findFoodNamesInDietHigherThanBloodSugar(Long writerId, int bloodSugar);

    /**
     * @param writerId 작성자 id
     * @return 작성자의 평균 혈당보다 높거나 같은 식단들에 기재된 음식들
     */
    List<String> findFoodHigherThanAverageBloodSugarOfDiet(Long writerId);

    /**
     * 입력 값에 해당하는 음식들 전부 "한꺼번에" 삭제하는 메서드
     *
     * @param foodIds 음식 id 리스트
     */
    void bulkDeleteFood(List<Long> foodIds);
}
