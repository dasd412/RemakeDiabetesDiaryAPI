package com.dasd412.remake.api.controller.security.domain_rest.dto.profile;

import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class ProfileUpdateResponseDTO {

    private final DiabetesPhase diabetesPhase;

    public ProfileUpdateResponseDTO(Profile profile) {
        this.diabetesPhase = profile.getDiabetesPhase();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("phase", diabetesPhase)
                .toString();
    }
}
