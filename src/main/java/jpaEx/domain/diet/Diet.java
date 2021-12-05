package jpaEx.domain.diet;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.food.Food;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Diet")
public class Diet extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diet_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private EatTime eatTime;

    private int bloodSugar;

    //양방향 연관 관계를 맺을 때는 외래키를 갖고 있는 쪽이 연관 관계의 주인이 되어야 한다.
    //db의 1 대 다 관계에서는 '다' 쪽이 외래키를 갖고 있다. 따라서 '다'에 해당하는 Food 가 연관 관계의 주인이 되어야 한다.
    //'일'에 해당하는 Diet 는 주인이 아니므로 mappedBy 속성을 사용하여 주인이 아님을 지정한다.
    @OneToMany(mappedBy = "diet")
    private List<Food>foodList=new ArrayList<>();

    protected Diet(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EatTime getEatTime() {
        return eatTime;
    }

    public void setEatTime(EatTime eatTime) {
        this.eatTime = eatTime;
    }

    public int getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(int bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }
}
