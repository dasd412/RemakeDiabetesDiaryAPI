/*
 * @(#)DiabetesPhase.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.profile;

/**
 * 당뇨 단계를 나타내는 enum
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
