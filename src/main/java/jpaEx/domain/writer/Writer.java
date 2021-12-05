package jpaEx.domain.writer;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diary.DiabetesDiary;
import jpaEx.domain.food.Food;

import javax.persistence.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name="Writer")
public class Writer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="writer_id")
    private Long id;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "writer",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<DiabetesDiary>diaries;

    //양방향 연관 관계를 맺을 때는 외래키를 갖고 있는 쪽이 연관 관계의 주인이 되어야 한다.
    //db의 1 대 다 관계에서는 '다' 쪽이 외래키를 갖고 있다. 따라서 '다'에 해당하는 Food 가 연관 관계의 주인이 되어야 한다.
    //'일'에 해당하는 Writer 는 주인이 아니므로 mappedBy 속성을 사용하여 주인이 아님을 지정한다.
    //orphanRemoval=true 로 지정하면, 부모 엔티티 컬렉션에서 자식 엔티티를 삭제할 때 참조가 끊어지면서 db 에도 삭제된다.
    //cascade = CascadeType.ALL 을 적용하면 이 엔티티에 적용된 작업이 연관된 다른 엔티티들에도 모두 적용이 전파된다.
    @OneToMany(mappedBy ="writer",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Food> foodList;

    protected Writer(){}

    public Writer(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkArgument(name.length()>0 && name.length()<=50, "name should be between 1 and 50");
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        //CHECK Email
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<DiabetesDiary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<DiabetesDiary> diaries) {
        this.diaries = diaries;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }

    public void addFood(Food food){
        this.foodList.add(food);
        //무한 루프 방지
        if(food.getWriter()!=this){
            food.setWriter(this);
        }
    }

    public void addDiaries(DiabetesDiary diary){
        this.diaries.add(diary);
        //무한 루프 방지
        if(diary.getWriter()!=this){
            diary.setWriter(this);
        }
    }
}
