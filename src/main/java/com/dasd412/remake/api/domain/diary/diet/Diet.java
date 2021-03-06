/*
 * @(#)Diet.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diet;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.food.Food;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "Diet", uniqueConstraints = @UniqueConstraint(columnNames = {"diet_id"}))
@IdClass(DietId.class)
public class Diet {

    @Id
    @Column(name = "diet_id", columnDefinition = "bigint default 0")
    private Long dietId;

    /**
     * referencedColumnName 를 지정해줘야 순서가 거꾸로 안나온다.
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "writer_id", referencedColumnName = "writer_id"),
            @JoinColumn(name = "diary_id", referencedColumnName = "diary_id")
    })
    private DiabetesDiary diary;

    @Enumerated(EnumType.STRING)
    private EatTime eatTime;

    private int bloodSugar;

    @OneToMany(mappedBy = "diet", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Food> foodList = new HashSet<>();

    public Diet() {
    }

    public Diet(EntityId<Diet, Long> dietEntityId, DiabetesDiary diary, EatTime eatTime, int bloodSugar) {
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");
        this.dietId = dietEntityId.getId();
        this.diary = diary;
        this.eatTime = eatTime;
        this.bloodSugar = bloodSugar;
    }

    public Long getDietId() {
        return dietId;
    }

    public EatTime getEatTime() {
        return eatTime;
    }

    private void modifyEatTime(EatTime eatTime) {
        this.eatTime = eatTime;
    }

    public int getBloodSugar() {
        return bloodSugar;
    }

    private void modifyBloodSugar(int bloodSugar) {
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");
        this.bloodSugar = bloodSugar;
    }

    public List<Food> getFoodList() {
        return new ArrayList<>(foodList);
    }

    public void addFood(Food food) {
        this.foodList.add(food);
        //무한 루프에 빠지지 않도록 체크
        if (food.getDiet() != this) {
            food.makeRelationWithDiet(this);
        }
    }

    public DiabetesDiary getDiary() {
        return diary;
    }

    /**
     * 연관 관계 편의 메소드.
     * 복합키와 관련된 메서드이므로 엔티티 관계 설정이후엔 호출하면 안된다.
     */
    public void makeRelationWithDiary(DiabetesDiary diary) {
        //무한 루프 체크
        this.diary = diary;
        if (!diary.getDietList().contains(this)) {
            diary.getDietList().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", dietId)
                .append("diary", diary)
                .append("eatTime", eatTime)
                .append("blood sugar", bloodSugar)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(diary, dietId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Diet target = (Diet) obj;
        return Objects.equals(this.dietId, target.dietId) && Objects.equals(this.diary, target.diary);
    }

    public void update(EatTime eatTime, int bloodSugar) {
        modifyEatTime(eatTime);
        modifyBloodSugar(bloodSugar);
    }

    /**
     * 연관 관계 제거 시에만 사용
     */
    public void removeFood(Food food) {
        checkArgument(this.foodList.contains(food), "this diet dose not have the food");
        this.foodList.remove(food);
    }
}
