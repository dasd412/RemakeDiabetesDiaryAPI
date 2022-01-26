package com.dasd412.remake.api.controller.security.domain_rest.dto.chart;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class FindAllFpgDTO {

    private final int fastingPlasmaGlucose;

    public FindAllFpgDTO(int fastingPlasmaGlucose) {
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fpg", this.fastingPlasmaGlucose)
                .toString();
    }
}
