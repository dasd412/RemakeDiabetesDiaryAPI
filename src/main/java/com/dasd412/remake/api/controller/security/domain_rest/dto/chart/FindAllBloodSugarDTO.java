package com.dasd412.remake.api.controller.security.domain_rest.dto.chart;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

@Getter
public class FindAllBloodSugarDTO {

    /**
     * y - 축
     */
    private final int bloodSugar;

    /**
     * x - 축
     */
    private final LocalDateTime dateTime;

    /**
     * 그래프 구분 용도
     */
    private final EatTime eatTime;

    public FindAllBloodSugarDTO(DiabetesDiary diary, Diet diet) {
        this.bloodSugar = diet.getBloodSugar();
        this.dateTime = diary.getWrittenTime();
        this.eatTime = diet.getEatTime();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("blood sugar", bloodSugar)
                .append("date ", dateTime)
                .append("eat time", eatTime)
                .toString();
    }
}
