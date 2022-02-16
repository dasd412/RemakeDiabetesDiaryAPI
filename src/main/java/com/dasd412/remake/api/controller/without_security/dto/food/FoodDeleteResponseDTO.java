package com.dasd412.remake.api.controller.without_security.dto.food;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FoodDeleteResponseDTO {

    private final Long writerId;

    private final Long diaryId;

    private final Long dietId;

    private final Long foodId;

    public FoodDeleteResponseDTO(Long writerId, Long diaryId, Long dietId, Long foodId) {
        this.writerId = writerId;
        this.diaryId=diaryId;
        this.dietId = dietId;
        this.foodId = foodId;
    }

    public Long getWriterId() { return writerId; }

    public Long getDiaryId() { return diaryId; }

    public Long getDietId() { return dietId; }

    public Long getFoodId() { return foodId; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("diary",diaryId)
                .append("diet", dietId)
                .append("food", foodId)
                .toString();
    }
}
