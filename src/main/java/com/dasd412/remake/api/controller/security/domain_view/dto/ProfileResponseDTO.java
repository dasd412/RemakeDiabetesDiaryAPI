package com.dasd412.remake.api.controller.security.domain_view.dto;

import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class ProfileResponseDTO {

    private final DiabetesPhase diabetesPhase;

    public ProfileResponseDTO(Profile profile) {

        this.diabetesPhase = profile.getDiabetesPhase();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("phase", diabetesPhase)
                .toString();
    }
}
