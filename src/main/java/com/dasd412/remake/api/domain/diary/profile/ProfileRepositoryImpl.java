/*
 * @(#)ProfileRepositoryImpl.java        1.1.1 2022/2/27
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.profile;

import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * Querydsl을 사용하기 위해 만든 구현체 클래스.
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 27일
 */
public class ProfileRepositoryImpl implements ProfileRepositoryCustom{
        /*
    fetch : 조회 대상이 여러건일 경우. 컬렉션 반환
    fetchOne : 조회 대상이 1건일 경우(1건 이상일 경우 에러). generic 에 지정한 타입으로 반환
    fetchFirst : 조회 대상이 1건이든 1건 이상이든 무조건 1건만 반환. 내부에 보면 return limit(1).fetchOne() 으로 되어있음
    fetchCount : 개수 조회. long 타입 반환
    fetchResults : 조회한 리스트 + 전체 개수를 포함한 QueryResults 반환. count 쿼리가 추가로 실행된다.
     */

    /**
     * Querydsl 쿼리 사용을 위한 객체
     */
    private final JPAQueryFactory jpaQueryFactory;

    public ProfileRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        this.jpaQueryFactory=jpaQueryFactory;
    }


}
