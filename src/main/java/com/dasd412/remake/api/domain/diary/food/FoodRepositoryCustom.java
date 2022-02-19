/*
 * @(#)FoodRepositoryCustom.java        1.0.9 2022/2/18
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;

import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 음식 리포지토리 상위 인터페이스. Querydsl을 이용하기 위해 구현하였다.
 *
 * @author 양영준
 * @version 1.0.9 2022년 2월 18일
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
     * @param predicates where 조건문 (높냐 낮냐, 날짜 사이인가 등..)
     * @return 조건에 맞는 음식 이름들
     */
    List<String> findFoodNamesInDiet(Long writerId, List<Predicate> predicates);

    /**
     * 입력 값에 해당하는 음식들 전부 "한꺼번에" 삭제하는 메서드
     *
     * @param foodIds 음식 id 리스트
     */
    void bulkDeleteFood(List<Long> foodIds);

    /**
     * decideEqualitySignOfBloodSugar(),decideBetweenTimeInDiary() 과 연계해서 쓰면 된다.
     *
     * @param writerId   작성자 id
     * @param predicates where 절의 조건문들.
     * @param pageable   페이징 객체
     * @return 해당 기간 동안 작성자가 작성한 음식에 관한 정보들
     */
    Page<FoodBoardDTO> findFoodsWithPagination(Long writerId, List<Predicate> predicates, Pageable pageable);

}
