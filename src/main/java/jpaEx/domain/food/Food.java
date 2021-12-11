package jpaEx.domain.food;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diet.Diet;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name="Food")
public class Food extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String foodName;

    //'다'에 해당하므로 연관 관계의 주인이다. 되도록이면 모든 연관 관계를 지연로딩으로 사용하는 것이 성능에 좋음.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="diet_id")
    private Diet diet;

    protected Food(){}

    public Food(String foodName){
        this(foodName,null);
    }

    public Food(String foodName,Diet diet){
        this.foodName=foodName;
        this.diet=diet;
    }

    public Long getId() {
        return id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void modifyFoodName(String foodName) {
        checkArgument(foodName.length()>0 && foodName.length()<=50, "food name length should be between 1 and 50");
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
        this.diet=diet;
        if(!diet.getFoodList().contains(this)){
            diet.getFoodList().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id",id)
                .append("foodName",foodName)
                .toString();
    }

}
