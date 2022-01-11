package com.dasd412.remake.api.controller.security.domain_view.dto;

import com.dasd412.remake.api.controller.security.domain_rest.dto.SecurityFoodDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.SecurityFoodForUpdateDTO;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Size;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostForUpdateDTO {

    //일지 정보
    private final Long diaryId;
    private final int fastingPlasmaGlucose;
    private final String writtenTime;
    private final String remark;

    //식단 정보
    private int breakFastSugar;
    private int lunchSugar;
    private int dinnerSugar;

    //음식 정보
    @Size(max = 5)
    private List<SecurityFoodForUpdateDTO> breakFastFoods;

    @Size(max = 5)
    private List<SecurityFoodForUpdateDTO> lunchFoods;

    @Size(max = 5)
    private List<SecurityFoodForUpdateDTO> dinnerFoods;

    public PostForUpdateDTO(DiabetesDiary targetDiary) {
        //unwrap diary
        this.diaryId = targetDiary.getId();
        this.fastingPlasmaGlucose = targetDiary.getFastingPlasmaGlucose();
        this.remark = targetDiary.getRemark();
        this.writtenTime = targetDiary.getWrittenTime().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //unwrap diet and food
        for (Diet diet : targetDiary.getDietList()) {
            switch (diet.getEatTime()) {
                case BreakFast:
                    this.breakFastSugar = diet.getBloodSugar();

                    this.breakFastFoods = diet.getFoodList()
                            .stream()
                            .map(food -> new SecurityFoodForUpdateDTO(food.getId(), food.getFoodName(), food.getAmount()))
                            .collect(Collectors.toList());
                    break;

                case Lunch:
                    this.lunchSugar = diet.getBloodSugar();

                    this.lunchFoods = diet.getFoodList()
                            .stream()
                            .map(food -> new SecurityFoodForUpdateDTO(food.getId(), food.getFoodName(), food.getAmount()))
                            .collect(Collectors.toList());

                    break;

                case Dinner:
                    this.dinnerSugar = diet.getBloodSugar();

                    this.dinnerFoods = diet.getFoodList()
                            .stream()
                            .map(food -> new SecurityFoodForUpdateDTO(food.getId(), food.getFoodName(), food.getAmount()))
                            .collect(Collectors.toList());
                    break;
            }
        }

    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("diaryId", diaryId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("writtenTime", writtenTime)
                .append("breakFastSugar", breakFastSugar)
                .append("lunchSugar", lunchSugar)
                .append("dinnerSugar", dinnerSugar)
                .append("breakFastFoods", breakFastFoods)
                .append("lunchFoods", lunchFoods)
                .append("dinnerFoods", dinnerFoods)
                .toString();
    }
}
