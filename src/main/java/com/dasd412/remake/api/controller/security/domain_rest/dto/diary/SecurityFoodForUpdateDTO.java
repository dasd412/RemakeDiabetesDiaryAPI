package com.dasd412.remake.api.controller.security.domain_rest.dto.diary;

import com.dasd412.remake.api.domain.diary.food.AmountUnit;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class SecurityFoodForUpdateDTO {
    //수정 및 삭제 시에는 id 값이 필요함.

    private final Long id;

    private final String foodName;

    private final double amount;

    private final String unit;

    public SecurityFoodForUpdateDTO(Long id, String foodName, double amount, AmountUnit unit) {
        this.id = id;
        this.foodName = foodName;
        this.amount = amount;
        if (unit.name().equals(AmountUnit.count.name())) {
            this.unit = "개";
        } else if (unit.name().equals(AmountUnit.NONE.name())) {
            this.unit = "";
        } else {
            this.unit = unit.name();
        }
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("foodId", id)
                .append("foodName", foodName)
                .append("amount", amount)
                .append("unit", unit)
                .toString();

    }
}
