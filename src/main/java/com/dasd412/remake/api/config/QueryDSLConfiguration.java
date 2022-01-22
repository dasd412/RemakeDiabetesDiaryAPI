/*
 * @(#)QueryDSLConfiguration.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Querydsl 설정과 관련된 클래스. JpaQueryFactory Bean 주입용으로 사용하고 있다.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

@Configuration
public class QueryDSLConfiguration {

    /**
     * Querydsl 작업할 때는 JPAQueryFactory Bean이 필요하다.
     * 그리고 생성할 때는 EntityManager를 주입해야 한다.
     */
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
