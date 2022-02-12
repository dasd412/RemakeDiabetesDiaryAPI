/*
 * @(#)InequalitySign.java        1.0.7 2022/2/12
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary;

/**
 * '>', '<', '<=', '==' ,'>=' 를 문자열이 아닌 Enum으로 규격화하기 위함.
 *
 * @author 양영준
 * @version 1.0.7 2022년 2월 12일
 */
public enum InequalitySign {

    GREATER,

    LESSER,

    GREAT_OR_EQUAL,

    EQUAL,

    LESSER_OR_EQUAL,

    NONE /* null 대응 */
}
