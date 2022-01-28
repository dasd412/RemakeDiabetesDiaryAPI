package com.dasd412.remake.api.controller.security.domain_rest.dto.chart;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

@Getter
public class FindFpgBetweenDTO {

    /**
     * y -축
     */
    private final int fastingPlasmaGlucose;

    /**
     * x -축
     */
    private final LocalDateTime timeByTimeStamp;

    public FindFpgBetweenDTO(DiabetesDiary diary) {
        this.fastingPlasmaGlucose = diary.getFastingPlasmaGlucose();
        this.timeByTimeStamp = diary.getWrittenTime();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fpg", this.fastingPlasmaGlucose)
                .append("timeStamp", this.timeByTimeStamp)
                .toString();
    }
}
