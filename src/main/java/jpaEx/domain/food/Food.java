package jpaEx.domain.food;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diet.Diet;

import javax.persistence.*;

@Entity
@Table(name="Food")
public class Food extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String foodName;

    //'다'에 해당하므로 연관 관계의 주인이다.
    @ManyToOne
    @JoinColumn(name="diet_id")
    private Diet diet;

    protected Food(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Diet getDiet() {
        return diet;
    }
    //연관 관계 편의 메소드
    public void setDiet(Diet diet) {
        //기존 관계 제거
        if(this.diet!=null){
            this.diet.getFoodList().remove(this);
        }
        this.diet=diet;
        diet.getFoodList().add(this);
    }
}
