package jpaEx.domain.diet;

import jpaEx.domain.diary.DiabetesDiaryId;

import java.io.Serializable;
import java.util.Objects;

//자식 id 클래스 (복합키)
public class DietId implements Serializable {

    private DiabetesDiaryId diary;//Diet.diary 매핑
    private Long id;//Diet.id 매핑

    public DietId() {
    }

    @Override
    public int hashCode() {
        return Objects.hash(diary, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DietId target = (DietId) obj;
        return Objects.equals(this.id, target.id) && Objects.equals(this.diary, target.diary);
    }

}
