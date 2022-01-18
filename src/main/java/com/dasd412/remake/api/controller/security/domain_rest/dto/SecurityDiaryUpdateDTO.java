package com.dasd412.remake.api.controller.security.domain_rest.dto;

import com.dasd412.remake.api.controller.security.FoodListSize;
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

    //기존 음식 엔티티들. 삭제 용이라서 id 존재
    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> oldBreakFastFoods;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> oldLunchFoods;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> oldDinnerFoods;

    //수정된 음식 엔티티들. 삽입 용이라서 id 없음.
    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodDTO> newBreakFastFoods;

    @Size(max =FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodDTO> newLunchFoods;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodDTO> newDinnerFoods;


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
                .append("oldBreakFastFoods", oldBreakFastFoods)
                .append("oldLunchFoods", oldLunchFoods)
                .append("oldDinnerFoods", oldDinnerFoods)
                .append("newBreakFastFoods", newBreakFastFoods)
                .append("newLunchFoods", newLunchFoods)
                .append("newDinnerFoods", newDinnerFoods)
                .toString();
    }
}
