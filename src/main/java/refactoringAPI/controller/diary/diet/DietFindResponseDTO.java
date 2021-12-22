package refactoringAPI.controller.diary.diet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import refactoringAPI.domain.diary.diet.Diet;
import refactoringAPI.domain.diary.diet.EatTime;

public class DietFindResponseDTO {

    private final Long writerId;

    private final Long diaryId;

    private final Long dietId;

    private final EatTime eatTime;

    private final int bloodSugar;

    public DietFindResponseDTO(Diet diet) {
        this.writerId = diet.getDiary().getWriter().getId();
        this.diaryId = diet.getDiary().getId();
        this.dietId = diet.getDietId();
        this.eatTime = diet.getEatTime();
        this.bloodSugar = diet.getBloodSugar();
    }

    public Long getWriterId() {
        return writerId;
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public Long getDietId() {
        return dietId;
    }

    public EatTime getEatTime() {
        return eatTime;
    }

    public int getBloodSugar() {
        return bloodSugar;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("diary", diaryId)
                .append("diet", dietId)
                .append("bloodSugar", bloodSugar)
                .append("eatTime", eatTime)
                .toString();
    }
}
