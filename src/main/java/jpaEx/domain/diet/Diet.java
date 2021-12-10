package jpaEx.domain.diet;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diary.DiabetesDiary;
import jpaEx.domain.food.Food;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

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
    @OneToMany(mappedBy = "diet",orphanRemoval = true,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    //orphanRemoval=true 로 지정하면, 부모 엔티티 컬렉션에서 자식 엔티티를 삭제할 때 참조가 끊어지면서 db 에도 삭제된다.
    //cascade = CascadeType.ALL 을 적용하면 이 엔티티에 적용된 작업이 연관된 다른 엔티티들에도 모두 적용이 전파된다.
    private List<Food>foodList=new ArrayList<>();

    //혈당일지 "일"에 대해 "다"이므로 연관관계의 주인(외래키 관리자)이다. 되도록이면 모든 연관 관계를 지연로딩으로 사용하는 것이 성능에 좋음.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="diary_id")
    private DiabetesDiary diary;

    protected Diet(){}

    public Diet(EatTime eatTime,int bloodSugar){
        this(eatTime,bloodSugar,null);
    }

    public Diet(EatTime eatTime, int bloodSugar, DiabetesDiary diary){
        this.eatTime=eatTime;
        this.bloodSugar=bloodSugar;
        this.diary=diary;
    }

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

    public void modifyBloodSugar(int bloodSugar) {
        checkArgument(bloodSugar > 0, "bloodSugar must be positive number");
        this.bloodSugar = bloodSugar;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void addFood(Food food){
        this.foodList.add(food);
        //무한 루프에 빠지지 않도록 체크
        if (food.getDiet()!=this){
            food.setDiet(this);
        }
    }

    public DiabetesDiary getDiary() {
        return diary;
    }

    //연관 관계 편의 메소드
    public void setDiary(DiabetesDiary diary) {
        //무한 루프 체크
        this.diary=diary;
        if(!diary.getDietList().contains(this)){
            diary.getDietList().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id",id)
                .append("eatTime",eatTime)
                .append("blood sugar",bloodSugar)
                .toString();
    }
}
