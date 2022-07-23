/*
 * @(#)WriterRepositoryImpl.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.writer;

import com.dasd412.remake.api.domain.diary.BulkDeleteHelper;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.Optional;

public class WriterRepositoryImpl implements WriterRepositoryCustom {

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

    /**
     * 이 메서드의 sql을 분석해보면, 알아서 inner join 해준다.
     */
    @Override
    public Optional<Profile> findProfile(Long writerId) {
        return Optional.ofNullable(
                jpaQueryFactory.select(QWriter.writer.profile)
                        .from(QWriter.writer)
                        .where(QWriter.writer.writerId.eq(writerId))
                        .fetchOne()
        );
    }

    @Override
    public Tuple findUserInfoByEmail(String email) {
        return jpaQueryFactory.select(QWriter.writer.name, QWriter.writer.provider)
                .from(QWriter.writer)
                .where(QWriter.writer.email.eq(email))
                .fetchOne();
    }

    @Override
    public Boolean existPassword(String email, String userName) {

        Integer fetchFirst = jpaQueryFactory
                .selectOne()
                .from(QWriter.writer)
                .where(QWriter.writer.name.eq(userName)
                        .and(QWriter.writer.email.eq(email))
                        .and(QWriter.writer.provider.isNull())) /* provider가 null일때만 비밀번호가 존재한다.*/
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public void updateWithTempPassword(String email, String userName, String tempPassWord) {
        jpaQueryFactory.update(QWriter.writer)
                .set(QWriter.writer.password, tempPassWord)
                .where(QWriter.writer.email.eq(email)
                        .and(QWriter.writer.name.eq(userName)))
                .execute();
    }


    @Override
    public void updatePassword(Long writerId, String encodePassword) {
        jpaQueryFactory.update(QWriter.writer)
                .set(QWriter.writer.password, encodePassword)
                .where(QWriter.writer.writerId.eq(writerId))
                .execute();
    }
}
