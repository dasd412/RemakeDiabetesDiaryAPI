package com.dasd412.remake.api.controller.diary.food;

import com.dasd412.remake.api.domain.diary.food.Food;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FoodUpdateResponseDTO {

    private final Long writerId;

    private final Long diaryId;

    private final Long dietId;

    private final Long foodId;

    private final String foodName;

    public FoodUpdateResponseDTO(Food food) {
        this.writerId = food.getDiet().getDiary().getWriter().getId();
        this.diaryId = food.getDiet().getDiary().getId();
        this.dietId = food.getDiet().getDietId();
        this.foodId = food.getId();
        this.foodName = food.getFoodName();
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

    public Long getFoodId() {
        return foodId;
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
                .append("food", foodId)
                .append("foodName", foodName)
                .toString();
    }
}
