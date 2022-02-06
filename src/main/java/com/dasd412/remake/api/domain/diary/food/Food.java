/*
 * @(#)Food.java        1.0.5 2022/2/6
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 음식 엔티티. 식단의 하위 엔티티
 *
 * @author 양영준
 * @version 1.0.5 2022년 2월 6일
 */
@Entity
@Table(name = "Food", uniqueConstraints = @UniqueConstraint(columnNames = {"food_id"}))
@IdClass(FoodId.class)
public class Food {

    /**
     * 식별 관계이므로 복합키 사용
     * 복합키의 경우 @GeneratedValue 사용 불가.
     */
    @Id
    @Column(name = "food_id", columnDefinition = "bigint default 0")
    private Long foodId;

    /**
     * '다'에 해당하므로 연관 관계의 주인이다. 되도록이면 모든 연관 관계를 지연로딩으로 사용하는 것이 성능에 좋음.
     * referencedColumnName 를 지정해줘야 순서가 거꾸로 안나온다.
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "writer_id", referencedColumnName = "writer_id"),
            @JoinColumn(name = "diary_id", referencedColumnName = "diary_id"),
            @JoinColumn(name = "diet_id", referencedColumnName = "diet_id")
    })
    private Diet diet;

    /**
     * 음식 이름
     */
    private String foodName;

    /**
     * 음식 수량
     */
    private double amount;

    /**
     * 음식 수량 단위. 음식 수량이랑 엮인다.
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(32) default 'NONE'")
    private AmountUnit amountUnit = AmountUnit.NONE;

    public Food() {
    }

    /**
     * 수량 작성하지 않고 음식 엔티티 저장.
     *
     * @param foodEntityId 래퍼로 감싸진 작성자 id
     * @param diet         연관 관계를 맺는 식단
     * @param foodName     음식 이름
     */
    public Food(EntityId<Food, Long> foodEntityId, Diet diet, String foodName) {
        this(foodEntityId, diet, foodName, 0);
    }

    /**
     * 음식 수량 단위가 추가되지 않았을 때 쓰였던 기존 생성자.
     *
     * @param foodEntityId 래퍼로 감싸진 작성자 id
     * @param diet         연관 관계를 맺는 식단
     * @param foodName     음식 이름
     * @param amount       음식 수량
     */
    public Food(EntityId<Food, Long> foodEntityId, Diet diet, String foodName, double amount) {
        this(foodEntityId, diet, foodName, amount, AmountUnit.NONE);
    }

    /**
     * 음식 수량 단위가 추가되고 나서 쓰이는 생성자.
     *
     * @param foodEntityId 래퍼로 감싸진 작성자 id
     * @param diet         연관 관계를 맺는 식단
     * @param foodName     음식 이름
     * @param amount       음식 수량
     * @param amountUnit   음식 수량 단위
     */
    public Food(EntityId<Food, Long> foodEntityId, Diet diet, String foodName, double amount, AmountUnit amountUnit) {
        checkArgument(foodName.length() > 0 && foodName.length() <= 50, "food name length should be between 1 and 50");
        checkArgument(amount >= 0, "amount must be positive.");
        this.foodId = foodEntityId.getId();
        this.diet = diet;
        this.foodName = foodName;
        this.amount = amount;
        this.amountUnit = amountUnit;
    }

    public Long getId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getAmount() {
        return amount;
    }

    public AmountUnit getAmountUnit() {
        return amountUnit;
    }

    private void modifyFoodName(String foodName) {
        checkArgument(foodName.length() > 0 && foodName.length() <= 50, "food name length should be between 1 and 50");
        this.foodName = foodName;
    }

    private void modifyAmount(double amount) {
        checkArgument(amount >= 0, "amount must be positive.");
        this.amount = amount;
    }

    private void modifyAmountUnit(AmountUnit amountUnit) {
        this.amountUnit = amountUnit;
    }

    public Diet getDiet() {
        return diet;
    }

    /**
     * 연관 관계 편의 메소드.
     * 복합키와 관련된 메서드이므로 엔티티 관계 설정이후엔 호출하면 안된다.
     *
     * @param diet 연관 관계를 맺을 식단 엔티티
     */
    public void makeRelationWithDiet(Diet diet) {
        /* 기존 관계 삭제 */
        this.diet.getFoodList().remove(this);

        /* 무한 루프 체크 */
        this.diet = diet;
        if (!diet.getFoodList().contains(this)) {
            diet.getFoodList().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", foodId)
                .append("diet", diet)
                .append("foodName", foodName)
                .append("amount", amount)
                .append("amountUnit", amountUnit)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(diet, foodId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Food target = (Food) obj;
        return Objects.equals(this.foodId, target.foodId) && Objects.equals(this.diet, target.diet);
    }

    public void update(String foodName) {
        modifyFoodName(foodName);
    }

    public void update(String foodName, double amount, AmountUnit amountUnit) {
        modifyFoodName(foodName);
        modifyAmount(amount);
        modifyAmountUnit(amountUnit);
    }

}
