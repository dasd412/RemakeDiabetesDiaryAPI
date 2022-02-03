package com.dasd412.remake.api.controller.security.domain_rest.dto.chart;

import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.querydsl.core.Tuple;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Objects;

@Getter
public class FindAverageBloodSugarDTO {

    /**
     * 아침 식사 평균 혈당
     */
    private double averageBreakFast;
    /**
     * 점심 식사 평균 혈당
     */
    private double averageLunch;
    /**
     * 저녁 식사 평균 혈당
     */
    private double averageDinner;

    /**
     * @param tupleList (식사 시간, 평균 식사 혈당)의 튜플
     */
    public FindAverageBloodSugarDTO(List<Tuple> tupleList) {
        for (Tuple tuple : tupleList) {

            EatTime eatTime = tuple.get(QDiet.diet.eatTime);
            Double average = tuple.get(QDiet.diet.bloodSugar.avg());

            Objects.requireNonNull(eatTime);
            Objects.requireNonNull(average);

            switch (eatTime) {
                case BreakFast:
                    this.averageBreakFast = average;
                    break;

                case Lunch:
                    this.averageLunch = average;
                    break;
                case Dinner:
                    this.averageDinner = average;
                    break;
            }
        }
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("averageBreakFast", averageBreakFast)
                .append("averageLunch", averageLunch)
                .append("averageDinner", averageDinner)
                .toString();
    }
}
