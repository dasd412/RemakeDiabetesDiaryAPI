/*
 * @(#)WriterRepositoryImpl.java        1.0.9 2022/2/17
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.writer;

import com.dasd412.remake.api.domain.diary.BulkDeleteHelper;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.Optional;

/**
 * Querydsl을 사용하기 위해 만든 구현체 클래스.
 *
 * @author 양영준
 * @version 1.0.9 2022년 2월 17일
 */
public class WriterRepositoryImpl implements WriterRepositoryCustom {

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

    public WriterRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * @return 식별자의 최댓값. 작성자 생성 시 id를 지정하기 위해 사용된다.
     */
    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QWriter.writer).select(QWriter.writer.writerId.max()).fetchOne();
    }

    /**
     * 회원 탈퇴 시 작성한 일지 및 연관된 하위 엔티티들 (식단, 음식) 모두 "한꺼번에" 삭제하는 메서드
     *
     * @param writerId 작성자 id
     */
    @Override
    public void bulkDeleteWriter(Long writerId) {
        BulkDeleteHelper deleteHelper = new BulkDeleteHelper(jpaQueryFactory);
        deleteHelper.bulkDeleteWriter(writerId);
    }

    @Override
    public Optional<Writer> findWriterByName(String name) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QWriter.writer).where(QWriter.writer.name.eq(name)).fetchOne());
    }

    /*
     * querydsl 에선 exists 사용시 count()를 사용하므로 총 몇건인 지 확인하기 위해 전체를 확인하는 추가 작업이 필요하다.
     * 따라서 Querydsl 이 기본적으로 제공하는 exists 는 성능 상 좋지 않다.
     * 대신 fetchFirst()를 사용하여 limit(1)의 효과를 낼 수 있도록 하면 성능이 개선된다.
     */
    @Override
    public Boolean existsName(String name) {
        Integer fetchFirst = jpaQueryFactory
                .selectOne()
                .from(QWriter.writer)
                .where(QWriter.writer.name.eq(name))
                .fetchFirst();/* 값이 없으면 0이 아니라 null 반환. */

        return fetchFirst != null;
    }

    /*
     *  OAuth 회원 가입 시에는 provider 와 email 둘 다를 알 필요가 있다.
     *  반면, 일반 회원 가입 시에는 email 만 알 필요가 있다. provider 는 null 이다.
     */
    @Override
    public Boolean existsEmail(String email, String provider) {
        Integer fetchFirst;
        if (provider == null) {
            fetchFirst = jpaQueryFactory
                    .selectOne()
                    .from(QWriter.writer)
                    .where(QWriter.writer.email.eq(email))
                    .fetchFirst();
        } else {
            /* 이메일이 같더라도 provider 가 다르면 다른 걸로 인식하게 하기 */
            fetchFirst = jpaQueryFactory
                    .selectOne()
                    .from(QWriter.writer)
                    .where(QWriter.writer.email.eq(email).and(QWriter.writer.provider.eq(provider)))
                    .fetchFirst();
        }
        return fetchFirst != null;
    }

}
