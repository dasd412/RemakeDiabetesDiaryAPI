package jpaEx.domain.food;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diet.Diet;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "Food")
@IdClass(FoodId.class)
public class Food extends BaseTimeEntity {
    @Id
    @Column(name = "food_id", columnDefinition = "bigint default 0 auto_increment")
    private Long id;

    //'다'에 해당하므로 연관 관계의 주인이다. 되도록이면 모든 연관 관계를 지연로딩으로 사용하는 것이 성능에 좋음.
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "writer_id", referencedColumnName = "writer_id"),
            @JoinColumn(name = "diary_id", referencedColumnName = "diary_id"),
            @JoinColumn(name = "diet_id", referencedColumnName = "diet_id")
    })//referencedColumnName 를 지정해줘야 순서가 거꾸로 안나온다.
    private Diet diet;

    private String foodName;

    public Food() {
    }

    public Food(Long id, Diet diet, String foodName) {
        this.id = id;
        this.diet = diet;
        this.foodName = foodName;
    }

    public Long getId() {
        return id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void modifyFoodName(String foodName) {
        checkArgument(foodName.length() > 0 && foodName.length() <= 50, "food name length should be between 1 and 50");
        this.foodName = foodName;
    }

    public Diet getDiet() {
        return diet;
    }

    //연관 관계 편의 메소드
    public void setDiet(Diet diet) {
        //기존 관계 삭제
        this.diet.getFoodList().remove(this);
        //무한 루프 체크
        this.diet = diet;
        if (!diet.getFoodList().contains(this)) {
            diet.getFoodList().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("foodName", foodName)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(diet, id);
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
        return Objects.equals(this.id, target.id) && Objects.equals(this.diet, target.diet);
    }

}
