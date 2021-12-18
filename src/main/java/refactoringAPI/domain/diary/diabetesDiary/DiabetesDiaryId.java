package refactoringAPI.domain.diary.diabetesDiary;

import java.io.Serializable;
import java.util.Objects;

//자식 id 클래스 (복합키)
public class DiabetesDiaryId implements Serializable {

    private Long writer;//DiabetesDiary.writer 매핑

    private Long diaryId;//DiabetesDiary.diaryId 매핑

    //식별자 클래스는 기본 생성자가 반드시 있어야 한다.
    public DiabetesDiaryId() {
    }

    //식별자 클래스는 equals 와 hashcode 를 재정의 해야한다.
    @Override
    public int hashCode() {
        return Objects.hash(writer, diaryId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DiabetesDiaryId target = (DiabetesDiaryId) obj;
        return Objects.equals(this.writer, target.writer) && Objects.equals(this.diaryId, target.diaryId);
    }
}
