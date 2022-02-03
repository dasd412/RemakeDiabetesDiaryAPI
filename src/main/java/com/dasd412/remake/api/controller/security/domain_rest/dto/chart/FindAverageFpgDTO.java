package com.dasd412.remake.api.controller.security.domain_rest.dto.chart;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class FindAverageFpgDTO {

    /**
     * 평균 공복 혈당
     */
    private final double averageFpg;

    public FindAverageFpgDTO(Double average) {
        this.averageFpg = average;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("averageFpg", averageFpg)
                .toString();
    }
}
