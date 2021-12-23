package com.dasd412.remake.api.controller.diary.food;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FoodRequestDTO {

    private final Long writerId;

    private final Long diaryId;

    private final Long dietId;

    private final String foodName;

    public FoodRequestDTO(Long writerId, Long diaryId, Long dietId, String foodName) {
        this.writerId = writerId;
        this.diaryId = diaryId;
        this.dietId = dietId;
        this.foodName = foodName;
    }

    public Long getWriterId() {
        return writerId;
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public Long getDietId() {
        return dietId;
    }

    public String getFoodName() {
        return foodName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("diary", diaryId)
                .append("diet", dietId)
                .append("food", foodName)
                .toString();
    }
}
