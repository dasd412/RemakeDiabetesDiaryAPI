/*
 * @(#)DietId.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diet;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiaryId;

import java.io.Serializable;
import java.util.Objects;


/**
 * 식단 복합키 식별자 클래스. 반드시 Serializable 구현해야 한다.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 * @serial
 */
public class DietId implements Serializable {

    /**
     * Diet.diary 매핑
     */
    private DiabetesDiaryId diary;

    /**
     * Diet.dietId 매핑
     */
    private Long dietId;

    /**
     * 식별자 클래스는 기본 생성자가 반드시 있어야 한다.
     */
    public DietId() {
    }

    /**
     * 식별자 클래스는 반드시 equals 와 hashcode 를 재정의 해야한다.
     */
    @Override
    public int hashCode() {
        return Objects.hash(diary, dietId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DietId target = (DietId) obj;
        return Objects.equals(this.dietId, target.dietId) && Objects.equals(this.diary, target.diary);
    }

}
