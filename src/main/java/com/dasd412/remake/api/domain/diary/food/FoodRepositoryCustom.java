/*
 * @(#)FoodRepositoryCustom.java
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

public interface FoodRepositoryCustom {

    /**
     * @return 식별자의 최댓값. 음식 생성 시 id를 지정하기 위해 사용된다. (복합키에는 @GeneratedValue 사용 불가.)
     */
    Long findMaxOfId();

    List<Food> findFoodsInDiet(Long writerId, Long dietId);

    Optional<Food> findOneFoodByIdInDiet(Long writerId, Long dietId, Long foodId);

    List<String> findFoodNamesInDietWithWhereClause(Long writerId, List<Predicate> predicates);

    void bulkDeleteFood(List<Long> foodIds);

    Page<FoodBoardDTO> findFoodsWithPaginationAndWhereClause(Long writerId, List<Predicate> predicates, Pageable pageable);

}
