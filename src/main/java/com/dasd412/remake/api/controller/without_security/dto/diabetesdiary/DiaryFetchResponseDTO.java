package com.dasd412.remake.api.controller.without_security.dto.diabetesdiary;

import com.dasd412.remake.api.controller.without_security.dto.diet.DietListFindResponseDTO;
import com.dasd412.remake.api.controller.without_security.dto.food.FoodListFindResponseDTO;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.food.Food;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiaryFetchResponseDTO {
    private final Long diaryId;

    private final Long writerId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    private final LocalDateTime writtenTime;

    private final List<DietListFindResponseDTO> dietList;

    private final List<FoodListFindResponseDTO> foodList;


    public DiaryFetchResponseDTO(DiabetesDiary diary) {
        this.diaryId = diary.getId();
        this.writerId = diary.getWriter().getId();
        this.fastingPlasmaGlucose = diary.getFastingPlasmaGlucose();
        this.remark = diary.getRemark();
        this.writtenTime = diary.getWrittenTime();

        List<Diet> diets = diary.getDietList();
        dietList = diets.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        List<Food> foods = new ArrayList<>();
        for (Diet diet : diets) {
            foods.addAll(diet.getFoodList());
        }
        foodList = foods.stream().map(
                FoodListFindResponseDTO::new
        ).collect(Collectors.toList());
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public Long getWriterId() {
        return writerId;
    }

    public int getFastingPlasmaGlucose() {
        return fastingPlasmaGlucose;
    }

    public String getRemark() {
        return remark;
    }

    public LocalDateTime getWrittenTime() {
        return writtenTime;
    }

    public List<DietListFindResponseDTO> getDietList() {
        return dietList;
    }

    public List<FoodListFindResponseDTO> getFoodList() {
        return foodList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("diary", diaryId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("written time", writtenTime)
                .toString();
    }
}
