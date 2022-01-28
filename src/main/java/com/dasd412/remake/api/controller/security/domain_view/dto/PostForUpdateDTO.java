package com.dasd412.remake.api.controller.security.domain_view.dto;

import com.dasd412.remake.api.controller.security.FoodListSize;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityFoodForUpdateDTO;
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

    /**
     * 일지 정보
     */
    private final Long diaryId;
    private final int fastingPlasmaGlucose;
    private final String writtenTime;
    private final String remark;

    /**
     * 식단 정보
     */
    private Long breakFastId;
    private int breakFastSugar;

    private Long lunchId;
    private int lunchSugar;

    private Long dinnerId;
    private int dinnerSugar;

    /**
     * 전체 음식 리스트
     */
    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private List<SecurityFoodForUpdateDTO> breakFastFoods;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private List<SecurityFoodForUpdateDTO> lunchFoods;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private List<SecurityFoodForUpdateDTO> dinnerFoods;

    /**
     * 음식 중량 있는 리스트
     */
    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> breakFastFoodsWithAmount;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> lunchFoodsWithAmount;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> dinnerFoodsWithAmount;

    /**
     * 음식 중량 없는 리스트
     */
    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> breakFastFoodsWithoutAmount;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> lunchFoodsWithoutAmount;

    @Size(max = FoodListSize.FOOD_LIST_SIZE)
    private final List<SecurityFoodForUpdateDTO> dinnerFoodsWithoutAmount;


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
                    this.breakFastId = diet.getDietId();
                    this.breakFastSugar = diet.getBloodSugar();

                    this.breakFastFoods = diet.getFoodList()
                            .stream()
                            .map(food -> new SecurityFoodForUpdateDTO(food.getId(), food.getFoodName(), food.getAmount()))
                            .collect(Collectors.toList());
                    break;

                case Lunch:
                    this.lunchId = diet.getDietId();
                    this.lunchSugar = diet.getBloodSugar();

                    this.lunchFoods = diet.getFoodList()
                            .stream()
                            .map(food -> new SecurityFoodForUpdateDTO(food.getId(), food.getFoodName(), food.getAmount()))
                            .collect(Collectors.toList());

                    break;

                case Dinner:
                    this.dinnerId = diet.getDietId();
                    this.dinnerSugar = diet.getBloodSugar();

                    this.dinnerFoods = diet.getFoodList()
                            .stream()
                            .map(food -> new SecurityFoodForUpdateDTO(food.getId(), food.getFoodName(), food.getAmount()))
                            .collect(Collectors.toList());
                    break;
            }
        }

        /*
          음식의 양 > 0 인 것은 수량이 존재하는 리스트에 따로 담기
         */
        this.breakFastFoodsWithAmount = breakFastFoods.stream().filter(food -> food.getAmount() > 0.0).collect(Collectors.toList());
        this.lunchFoodsWithAmount = lunchFoods.stream().filter(food -> food.getAmount() > 0.0).collect(Collectors.toList());
        this.dinnerFoodsWithAmount = dinnerFoods.stream().filter(food -> food.getAmount() > 0.0).collect(Collectors.toList());

        /*
            음식의 양 == 0인 것은 수량이 없는 것에 따로 담기
         */
        this.breakFastFoodsWithoutAmount = breakFastFoods.stream().filter(food -> food.getAmount() == 0.0).collect(Collectors.toList());
        this.lunchFoodsWithoutAmount = lunchFoods.stream().filter(food -> food.getAmount() == 0.0).collect(Collectors.toList());
        this.dinnerFoodsWithoutAmount = dinnerFoods.stream().filter(food -> food.getAmount() == 0.0).collect(Collectors.toList());

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
                .append("breakFastFood", breakFastFoods)
                .append("lunchFood", lunchFoods)
                .append("dinnerFood", dinnerFoods)
                .toString();
    }
}
