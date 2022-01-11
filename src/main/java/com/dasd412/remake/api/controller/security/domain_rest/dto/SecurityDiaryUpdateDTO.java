package com.dasd412.remake.api.controller.security.domain_rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Size;
import java.util.List;

@RequiredArgsConstructor
@Builder
@Getter
public class SecurityDiaryUpdateDTO {

    //일지
    private final Long diaryId;
    private final int fastingPlasmaGlucose;
    private final String remark;
    private final boolean diaryDirty; //<-실제로 변경되었는가.

    //식단
    private final Long breakFastId;
    private final int breakFastSugar;
    private final boolean breakFastDirty;

    private final Long lunchId;
    private final int lunchSugar;
    private final boolean lunchDirty;

    private final Long dinnerId;
    private final int dinnerSugar;
    private final boolean dinnerDirty;

    //음식 (id,음식이름, 양)의 형태. 만약 id가 null 이면 새로이 만들어진 것.
    @Size(max = 5)
    private final List<SecurityFoodForUpdateDTO> breakFastFoods;

    @Size(max = 5)
    private final List<SecurityFoodForUpdateDTO> lunchFoods;

    @Size(max = 5)
    private final List<SecurityFoodForUpdateDTO> dinnerFoods;

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("diaryId", diaryId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("isDiaryDirty", diaryDirty)
                .append("breakFastId", breakFastId)
                .append("breakFastSugar", breakFastSugar)
                .append("isBreakFastDirty", breakFastDirty)
                .append("lunchId", lunchId)
                .append("lunchSugar", lunchSugar)
                .append("isLunchDirty", lunchDirty)
                .append("dinnerId", dinnerId)
                .append("dinnerSugar", dinnerSugar)
                .append("isDinnerDirty", dinnerDirty)
                .append("breakFastFoods", breakFastFoods)
                .append("lunchFoods", lunchFoods)
                .append("dinnerFoods", dinnerFoods)
                .toString();
    }
}
