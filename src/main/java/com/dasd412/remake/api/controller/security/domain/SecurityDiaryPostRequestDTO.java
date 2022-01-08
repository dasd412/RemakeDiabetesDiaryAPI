package com.dasd412.remake.api.controller.security.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class SecurityDiaryPostRequestDTO {

    private final int fastingPlasmaGlucose;

    private final String remark;

    @NotEmpty
    @NotNull
    private final String year;

    @NotEmpty
    @NotNull
    private final String month;

    @NotEmpty
    @NotNull
    private final String day;

    @NotEmpty
    @NotNull
    private final String hour;

    @NotEmpty
    @NotNull
    private final String minute;

    @NotEmpty
    @NotNull
    private final String second;

    private final int breakFastSugar;
    private final int lunchSugar;
    private final int dinnerSugar;

    private final List<SecurityFoodDTO> breakFastFoods;
    private final List<SecurityFoodDTO> lunchFoods;
    private final List<SecurityFoodDTO> dinnerFoods;

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("year", year)
                .append("month", month)
                .append("day", day)
                .append("hour", hour)
                .append("minute", minute)
                .append("second", second)
                .append("breakFastSugar", breakFastSugar)
                .append("lunchSugar", lunchSugar)
                .append("dinnerSugar", dinnerSugar)
                .append("breakFastFoods", breakFastFoods)
                .append("lunchFoods", lunchFoods)
                .append("dinnerFoods", dinnerFoods)
                .toString();
    }
}
