/*
 * @(#)Profile.java        1.1.1 2022/2/27
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.profile;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Objects;

/**
 * 프로필 엔티티. 작성자의 정보(당뇨 단계)등을 기입하는데 쓰인다.
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 27일
 */
@Entity
public class Profile {

    /**
     * 프로필 식별자
     * 복합키 아니므로 id 자동 할당 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    /**
     * 작성자의 당뇨 단계
     */
    @Enumerated(EnumType.STRING)
    private DiabetesPhase diabetesPhase;

    public Profile() {
    }

    public Profile(DiabetesPhase diabetesPhase) {
        this.diabetesPhase = diabetesPhase;
    }

    public DiabetesPhase getDiabetesPhase() {
        return diabetesPhase;
    }

    public void modifyDiabetesPhase(DiabetesPhase diabetesPhase) {
        this.diabetesPhase = diabetesPhase;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("profile_id", profileId)
                .append("diabetes phase", diabetesPhase)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Profile target = (Profile) obj;
        return Objects.equals(this.profileId, target.profileId);
    }

}
