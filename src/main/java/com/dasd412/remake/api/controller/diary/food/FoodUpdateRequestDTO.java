package com.dasd412.remake.api.controller.diary.food;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FoodUpdateRequestDTO {

    private final Long writerId;

    private final Long dietId;

    private final Long foodId;

    private final String foodName;

    //cannot deserialize from Object value (no delegate- or property-based Creator) 에러 때문에 기본 생성자 만듬.
    private FoodUpdateRequestDTO(){
        this.writerId = null;
        this.dietId = null;
        this.foodId = null;
        this.foodName = null;
    }

    public FoodUpdateRequestDTO(Long writerId, Long dietId, Long foodId, String foodName) {
        this.writerId = writerId;
        this.dietId = dietId;
        this.foodId = foodId;
        this.foodName = foodName;
    }

    public Long getWriterId() {
        return writerId;
    }

    public Long getDietId() {
        return dietId;
    }

    public Long getFoodId() { return foodId; }

    public String getFoodName() {
        return foodName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("diet", dietId)
                .append("food", foodId)
                .append("foodName", foodName)
                .toString();
    }
}
