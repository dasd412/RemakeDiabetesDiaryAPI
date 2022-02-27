/*
 * @(#)ProfileRepository.java        1.1.1 2022/2/27
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 프로필 리포지토리 인터페이스. ProfileRepositoryCustom 메서드 상속받아서 사용함.
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 27일
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long>,ProfileRepositoryCustom {
    
}
