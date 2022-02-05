/*
 * @(#)Food.java        1.0.5 2022/2/5
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;


/**
 * 음식 엔티티의 수량 단위를 나타내는 enum
 *
 * @author 양영준
 * @version 1.0.5 2022년 2월 5일
 */
public enum AmountUnit {
    /* 개수 */
    count,

    /* 그램  (디폴트 값. 기존 것과 호환하기 위함)*/
    g,

    /* 킬로그램 */
    kg,

    /* 리터 */
    L,

    /* 밀리리터 */
    mL
}
