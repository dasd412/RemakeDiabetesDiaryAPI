package jpaEx.domain.food;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diet.Diet;
import jpaEx.domain.writer.Writer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="writer_id")
    private Writer writer;

    protected Food(){}

    public Food(String foodName){
        this(foodName,null,null);
    }

    public Food(String foodName,Diet diet){
        this(foodName,diet,null);
    }

    public Food(String foodName,Diet diet, Writer writer){
        this.foodName=foodName;
        this.diet=diet;
        this.writer=writer;
    }

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
        //무한 루프 체크
        this.diet=diet;
        if(!diet.getFoodList().contains(this)){
            diet.getFoodList().add(this);
        }
    }

    public Writer getWriter() {
        return writer;
    }

    //연관 관계 편의 메소드
    public void setWriter(Writer writer) {
        //무한 루프 체크
        this.writer=writer;
        if(!writer.getFoodList().contains(this)){
            writer.getFoodList().add(this);
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
