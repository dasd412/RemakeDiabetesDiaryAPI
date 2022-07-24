/*
 * @(#)DiabetesDiaryId.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diabetesDiary;

import java.io.Serializable;
import java.util.Objects;

/**
 * 혈당일지 복합키 식별자 클래스. 반드시 Serializable 구현해야 한다.
 * @serial
 */
public class DiabetesDiaryId implements Serializable {

    /**
     * DiabetesDiary.writer 매핑
     */
    private Long writer;


    /**
     * DiabetesDiary.diaryId 매핑
     */
    private Long diaryId;

    /**
     * 식별자 클래스는 기본 생성자가 반드시 있어야 한다.
     */
    public DiabetesDiaryId() {
    }

    /**
     * 식별자 클래스는 반드시 equals 와 hashcode 를 재정의 해야한다.
     */
    @Override
    public int hashCode() {
        return Objects.hash(writer, diaryId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DiabetesDiaryId target = (DiabetesDiaryId) obj;
        return Objects.equals(this.writer, target.writer) && Objects.equals(this.diaryId, target.diaryId);
    }
}
