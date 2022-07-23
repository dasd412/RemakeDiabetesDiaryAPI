/*
 * @(#)Profile.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.profile;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @Enumerated(EnumType.STRING)
    private DiabetesPhase diabetesPhase;

    public Profile() {
    }

    public Profile(DiabetesPhase diabetesPhase) {
        this.diabetesPhase = diabetesPhase;
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
