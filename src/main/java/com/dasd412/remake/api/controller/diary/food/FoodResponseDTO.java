package com.dasd412.remake.api.controller.diary.food;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.dasd412.remake.api.domain.diary.food.Food;

public class FoodResponseDTO {

    private final Long foodId;

    private final String foodName;

    public FoodResponseDTO(Food food) {
        this.foodId = food.getId();
        this.foodName = food.getFoodName();
    }

    public Long getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("food", foodId)
                .append("foodName", foodName)
                .toString();
    }
}
