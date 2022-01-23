/*
 * @(#)FoodRepository.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 음식 리포지토리 인터페이스. FoodRepositoryCustom 메서드 상속받아서 사용함.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Long>,FoodRepositoryCustom {
     /*
    JPQL 조인은 데이터베이스 테이블의 칼럼을 기준으로 하지 않는다.
    엔티티의 연관 필드를 기준으로 해서 작성해야 한다!!
     */
}
