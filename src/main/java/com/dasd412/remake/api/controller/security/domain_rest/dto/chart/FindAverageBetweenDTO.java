package com.dasd412.remake.api.controller.security.domain_rest.dto.chart;

import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.querydsl.core.Tuple;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Objects;

@Getter
public class FindAverageBetweenDTO {

    /**
     * 기간 내 평균 공복 혈당
     */
    private final double averageFpgBetween;

    /**
     * 기간 내 아침 식사 평균 혈당
     */
    private double averageBreakFastBetween;
    /**
     * 기간 내 점심 식사 평균 혈당
     */
    private double averageLunchBetween;
    /**
     * 기간 내 저녁 식사 평균 혈당
     */
    private double averageDinnerBetween;

    /**
     * 기간 내 전체 식사 평균 혈당
     */
    private final double averageBloodSugarBetween;

    /**
     * @param averageFpgBetween        기간 내 평균 공복 혈당
     * @param tupleListBetween         기간 내 (식사 시간, 평균 식사 혈당)의 튜플
     * @param averageBloodSugarBetween 기간 내 전체 식사 평균 혈당
     */
    @Builder
    public FindAverageBetweenDTO(Double averageFpgBetween, List<Tuple> tupleListBetween, Double averageBloodSugarBetween) {
        this.averageFpgBetween = averageFpgBetween;

        for (Tuple tuple : tupleListBetween) {

            EatTime eatTime = tuple.get(QDiet.diet.eatTime);
            Double average = tuple.get(QDiet.diet.bloodSugar.avg());

            Objects.requireNonNull(eatTime);
            Objects.requireNonNull(average);

            switch (eatTime) {
                case BreakFast:
                    this.averageBreakFastBetween = average;
                    break;

                case Lunch:
                    this.averageLunchBetween = average;
                    break;
                case Dinner:
                    this.averageDinnerBetween = average;
                    break;
            }
        }

        this.averageBloodSugarBetween = averageBloodSugarBetween;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("averageFpgBetween", averageFpgBetween)
                .append("averageBreakFastBetween", averageBreakFastBetween)
                .append("averageLunchBetween", averageLunchBetween)
                .append("averageDinnerBetween", averageDinnerBetween)
                .append("averageBloodSugarBetween", averageBloodSugarBetween)
                .toString();
    }
}
