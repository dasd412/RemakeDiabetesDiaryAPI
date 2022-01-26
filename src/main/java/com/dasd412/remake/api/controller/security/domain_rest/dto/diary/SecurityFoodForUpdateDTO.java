package com.dasd412.remake.api.controller.security.domain_rest.dto.diary;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class SecurityFoodForUpdateDTO {
    //수정 / 삭제 시에는 id 값이 필요함.

    private final Long id;

    private final String foodName;

    private final double amount;

    public SecurityFoodForUpdateDTO(Long id, String foodName, double amount) {
        this.id = id;
        this.foodName = foodName;
        this.amount = amount;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("foodId", id)
                .append("foodName", foodName)
                .append("amount", amount)
                .toString();

    }
}
