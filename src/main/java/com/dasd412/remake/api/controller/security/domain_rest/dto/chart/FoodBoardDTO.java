package com.dasd412.remake.api.controller.security.domain_rest.dto.chart;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

@Getter
public class FoodBoardDTO {

    private final String foodName;
    private final int bloodSugar;
    private final LocalDateTime writtenTime;

    public FoodBoardDTO(String foodName, int bloodSugar, LocalDateTime writtenTime) {
        this.foodName = foodName;
        this.bloodSugar = bloodSugar;
        this.writtenTime = writtenTime;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("foodName", foodName)
                .append("bloodSugar", bloodSugar)
                .append("writtenTime", writtenTime)
                .toString();
    }
}
