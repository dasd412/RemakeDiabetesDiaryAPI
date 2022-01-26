package com.dasd412.remake.api.controller.security.domain_rest.dto.diary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SecurityFoodDTO {

    private final String foodName;
    private final double amount;

    public SecurityFoodDTO(String foodName, double amount) {
        this.foodName = foodName;
        this.amount = amount;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getAmount() {
        return amount;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("foodName", foodName)
                .append("amount", amount)
                .toString();

    }
}
