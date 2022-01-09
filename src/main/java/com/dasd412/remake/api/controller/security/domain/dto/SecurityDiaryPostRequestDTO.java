package com.dasd412.remake.api.controller.security.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@Builder
public class SecurityDiaryPostRequestDTO {

    private final Integer fastingPlasmaGlucose;

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

    private final Integer breakFastSugar;
    private final Integer lunchSugar;
    private final Integer dinnerSugar;

    private final List<SecurityFoodDTO> breakFastFoods;
    private final List<SecurityFoodDTO> lunchFoods;
    private final List<SecurityFoodDTO> dinnerFoods;

    //혈당은 INTEGER 타입으로 데이터 베이스에 기록된다. 그런데 제약조건이 0을 반드시 넘어야 한다.
    //하지만 폼 입력에서 데이터가 날라올 때, 모든 혈당이 기록된다는 보장은 없다.
    //프론트엔드 단에서 공백으로 온다면 json 변환할 때 정수는 자동 0이 된다.
    //따라서 두 개의 충돌을 피하기 위해 혈당 값이 0이면 null 로 바꾼다.
    //데이터베이스에서 기본키가 아닌 값은 null 이여도 상관없다.
    public Integer getFastingPlasmaGlucose() {
        if (this.fastingPlasmaGlucose == 0) {
            return null;
        }
        return fastingPlasmaGlucose;
    }

    public String getRemark() {
        return remark;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public String getSecond() {
        return second;
    }

    public Integer getBreakFastSugar() {
        if (this.breakFastSugar == 0) {
            return null;
        }
        return breakFastSugar;
    }

    public Integer getLunchSugar() {
        if (this.lunchSugar == 0) {
            return null;
        }
        return lunchSugar;
    }

    public Integer getDinnerSugar() {
        if (this.breakFastSugar == 0) {
            return null;
        }
        return dinnerSugar;
    }

    public List<SecurityFoodDTO> getBreakFastFoods() {
        return breakFastFoods;
    }

    public List<SecurityFoodDTO> getLunchFoods() {
        return lunchFoods;
    }

    public List<SecurityFoodDTO> getDinnerFoods() {
        return dinnerFoods;
    }

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
