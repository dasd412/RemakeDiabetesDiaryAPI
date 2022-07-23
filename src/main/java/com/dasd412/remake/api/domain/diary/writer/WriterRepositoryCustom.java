/*
 * @(#)WriterRepositoryCustom.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.writer;

import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.querydsl.core.Tuple;

import java.util.Optional;

public interface WriterRepositoryCustom {

    /**
     * @return 식별자의 최댓값. 작성자 생성 시 id를 지정하기 위해 사용된다.
     */
    Long findMaxOfId();

    void bulkDeleteWriter(Long writerId);

    Optional<Writer> findWriterByName(String name);

    Boolean existsName(String name);

    Boolean existsEmail(String email, String provider);

    Optional<Profile> findProfile(Long writerId);

    Tuple findUserInfoByEmail(String email);

    Boolean existPassword(String email, String userName);

    void updateWithTempPassword(String email, String userName, String tempPassWord);

    /**
     * @param encodePassword 변경하고자 하는 비밀 번호 (인코딩 필수!)
     */
    void updatePassword(Long writerId, String encodePassword);

}
