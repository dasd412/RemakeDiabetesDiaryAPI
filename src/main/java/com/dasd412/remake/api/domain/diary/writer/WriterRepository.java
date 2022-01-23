/*
 * @(#)WriterRepository.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.writer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 작성자 리포지토리 인터페이스. WriterRepositoryCustom 메서드 상속받아서 사용함.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@Repository
public interface WriterRepository extends JpaRepository<Writer, Long>, WriterRepositoryCustom {
    /*
    JPQL 에서 SELECT 절에 조회할 대상을 지정하는 것을 프로젝션(projection)이라 한다.
    SELECT 프로젝션 대상 FROM 으로 대상을 선택한다.
    엔티티, 임베디드 타입, 스칼라 타입이 프로젝션 대상으로 될 수 있다.
     */

    /*
    JPQL 조인은 데이터베이스 테이블의 칼럼을 기준으로 하지 않는다.
    엔티티의 연관 필드를 기준으로 해서 작성해야 한다!!
     */
}
