package jpaEx.domain.food;

import jpaEx.domain.diet.DietId;

import java.io.Serializable;
import java.util.Objects;

//자식 id 클래스 (복합키)
public class FoodId implements Serializable {

    private DietId diet;//Food.diet 매핑
    private Long id;//Food.id 매핑

    public FoodId(){}

    @Override
    public int hashCode() {
        return Objects.hash(diet,id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FoodId target = (FoodId) obj;
        return Objects.equals(this.id,target.id)&&Objects.equals(this.diet,target.diet);
    }

}
