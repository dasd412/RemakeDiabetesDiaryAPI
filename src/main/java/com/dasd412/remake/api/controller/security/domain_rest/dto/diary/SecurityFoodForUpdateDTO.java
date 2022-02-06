package com.dasd412.remake.api.controller.security.domain_rest.dto.diary;

import com.dasd412.remake.api.domain.diary.food.AmountUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@NoArgsConstructor /* <- cannot deserialize from Object value (no delegate- or property-based Creator) 에러를 막기 위해 넣음. */
public class SecurityFoodForUpdateDTO {
    //수정 및 삭제 시에는 id 값이 필요함.

    private Long id;

    private String foodName;

    private double amount;

    private String unit;

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
