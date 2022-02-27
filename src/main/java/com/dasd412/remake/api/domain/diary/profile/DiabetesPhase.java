/*
 * @(#)DiabetesPhase.java        1.1.1 2022/2/27
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.profile;

/**
 * 당뇨 단계를 나타내는 enum
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 27일
 */
public enum DiabetesPhase {
    /**
     * 정상 (공복 혈당 100 미만)
     */
    NORMAL,
    /**
     * 전당뇨 (공복 혈당 100 이상 125 이하)
     */
    PRE_DIABETES,
    /**
     * 당뇨 (공복 혈당 126 이상)
     */
    DIABETES
}
